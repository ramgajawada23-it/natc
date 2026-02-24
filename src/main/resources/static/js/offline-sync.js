/**
 * Offline Sync Manager
 * Handles IndexedDB storage and server synchronization
 */

const DB_NAME = 'DeptEmpOfflineDB';
const DB_VERSION = 1;
let db;

// Initialize Database
const initDB = () => {
    return new Promise((resolve, reject) => {
        const request = indexedDB.open(DB_NAME, DB_VERSION);

        request.onupgradeneeded = (event) => {
            const db = event.target.result;
            if (!db.objectStoreNames.contains('sync_queue')) {
                db.createObjectStore('sync_queue', { keyPath: 'id', autoIncrement: true });
            }
            if (!db.objectStoreNames.contains('departments')) {
                db.createObjectStore('departments', { keyPath: 'id' });
            }
            if (!db.objectStoreNames.contains('employees')) {
                db.createObjectStore('employees', { keyPath: 'id' });
            }
        };

        request.onsuccess = (event) => {
            db = event.target.result;
            resolve(db);
        };

        request.onerror = (event) => reject('IndexedDB error: ' + event.target.errorCode);
    });
};

// Add to Sync Queue
const addToSyncQueue = async (type, action, data) => {
    const transaction = db.transaction(['sync_queue'], 'readwrite');
    const store = transaction.objectStore('sync_queue');
    await store.add({ type, action, data, timestamp: new Date().getTime() });
    showNotification('Action saved offline. Will sync when online.', 'info');
};

// Sync Data with Server
const syncData = async () => {
    if (!navigator.onLine) return;

    const transaction = db.transaction(['sync_queue'], 'readwrite');
    const store = transaction.objectStore('sync_queue');
    const request = store.getAll();

    request.onsuccess = async () => {
        const queue = request.result;
        if (queue.length === 0) return;

        showNotification('Syncing offline changes...', 'info');

        for (const item of queue) {
            try {
                let url = item.type === 'department' ? '/api/departments' : '/api/employees';
                let method = item.action === 'create' ? 'POST' : (item.action === 'update' ? 'PUT' : 'DELETE');

                if (item.action !== 'create') {
                    url += `/${item.data.id}`;
                }

                const response = await fetch(url, {
                    method: method,
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(item.data)
                });

                if (response.ok) {
                    const delTransaction = db.transaction(['sync_queue'], 'readwrite');
                    delTransaction.objectStore('sync_queue').delete(item.id);
                }
            } catch (error) {
                console.error('Sync failed for item:', item, error);
            }
        }
        showNotification('Sync complete!', 'success');
        setTimeout(() => location.reload(), 2000);
    };
};

// Notification Helper
const showNotification = (message, type) => {
    const toastContainer = document.getElementById('toast-container') || createToastContainer();
    const toast = document.createElement('div');
    toast.className = `alert alert-${type} mt-2`;
    toast.innerHTML = `<i class="bi bi-info-circle"></i> ${message}`;
    toastContainer.appendChild(toast);
    setTimeout(() => toast.remove(), 5000);
};

const createToastContainer = () => {
    const container = document.createElement('div');
    container.id = 'toast-container';
    container.style.position = 'fixed';
    container.style.top = '20px';
    container.style.right = '20px';
    container.style.zIndex = '9999';
    document.body.appendChild(container);
    return container;
};

// Intercept Form Submissions
document.addEventListener('submit', async (e) => {
    const form = e.target;
    if (form.method.toLowerCase() !== 'post') return;

    // If online, let the regular request flow
    if (navigator.onLine) return;

    e.preventDefault();

    const formData = new FormData(form);
    const data = Object.fromEntries(formData.entries());

    // If ID is missing but in URL (for delete/update), try to extract it
    if (!data.id) {
        const idMatch = form.action.match(/\d+$/);
        if (idMatch) data.id = idMatch[0];
    }

    const isDept = form.action.includes('departments');
    const type = isDept ? 'department' : 'employee';
    let action = 'create';

    if (form.action.includes('update')) action = 'update';
    if (form.action.includes('delete')) action = 'delete';

    await addToSyncQueue(type, action, data);

    if (action === 'delete') {
        showNotification(`${type.charAt(0).toUpperCase() + type.slice(1)} marked for deletion.`, 'warning');
        // Visually hide the row for better UX
        const row = form.closest('tr');
        if (row) row.style.opacity = '0.3';
    } else {
        showNotification(`${type.charAt(0).toUpperCase() + type.slice(1)} saved offline!`, 'success');
    }

    if (action === 'create' || action === 'update') {
        form.reset();
    }
});

// Cache Refresh (Run when online to keep local data fresh)
const refreshLocalCache = async () => {
    if (!navigator.onLine) return;

    try {
        const [deptRes, empRes] = await Promise.all([
            fetch('/api/departments'),
            fetch('/api/employees')
        ]);

        if (!deptRes.ok || !empRes.ok) throw new Error('Failed to fetch from API');

        const depts = await deptRes.json();
        const emps = await empRes.json();

        const tx = db.transaction(['departments', 'employees'], 'readwrite');
        const deptStore = tx.objectStore('departments');
        const empStore = tx.objectStore('employees');

        // Clear old cache (optional, but cleaner)
        deptStore.clear();
        empStore.clear();

        depts.forEach(d => deptStore.put(d));
        emps.forEach(e => empStore.put(e));

        console.log('Local data cache refreshed from server');
    } catch (err) {
        console.warn('Cache refresh failed:', err);
    }
};

// Listen for Online/Offline status
window.addEventListener('online', async () => {
    await syncData();
    await refreshLocalCache();
});

window.addEventListener('load', async () => {
    await initDB();
    if (navigator.onLine) {
        await syncData();
        await refreshLocalCache();
    }

    // Register Service Worker (Disabled temporarily for troubleshooting)
    /*
    if ('serviceWorker' in navigator) {
        navigator.serviceWorker.register('/sw.js')
            .then(() => console.log('Service Worker Registered'))
            .catch(err => console.error('SW Registration Failed', err));
    }
    */
});
