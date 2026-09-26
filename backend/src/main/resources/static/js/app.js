// JWT Token Management
const Auth = {
    getToken() {
        return this.getCookie('jwt');
    },
    getCookie(name) {
        const value = `; ${document.cookie}`;
        const parts = value.split(`; ${name}=`);
        if (parts.length === 2) return parts.pop().split(';').shift();
        return null;
    },
    setToken(token) {
        document.cookie = `jwt=${token}; path=/; max-age=86400; SameSite=Strict`;
    },
    clearToken() {
        document.cookie = 'jwt=; path=/; max-age=0';
    },
    async apiCall(url, options = {}) {
        const token = this.getToken();
        const headers = { 'Content-Type': 'application/json', ...options.headers };
        if (token) headers['Authorization'] = `Bearer ${token}`;
        const response = await fetch(url, { ...options, headers });
        
        const contentType = response.headers.get('content-type');
        if (contentType && contentType.includes('text/html')) {
            throw new Error('Need to login');
        }

        if (!response.ok) {
            const errorData = await response.json().catch(() => ({}));
            throw new Error(errorData.message || `HTTP error! status: ${response.status}`);
        }
        return response.json();
    }
};

// Login Handler 
async function handleLogin(event) {
    event.preventDefault();
    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;
    const errorDiv = document.getElementById('error-message');
    try {
        const data = await Auth.apiCall('/api/auth/login', {
            method: 'POST',
            body: JSON.stringify({ username, password })
        });
        Auth.setToken(data.token);
        window.location.href = '/dashboard';
    } catch (err) {
        if (errorDiv) {
            errorDiv.textContent = err.message || 'Invalid credentials';
            errorDiv.style.display = 'block';
        }
    }
}

//  Register Handler 
async function handleRegister(event) {
    event.preventDefault();
    const username = document.getElementById('username').value;
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;
    const role = document.getElementById('role').value;
    const errorDiv = document.getElementById('error-message');
    try {
        const data = await Auth.apiCall('/api/auth/register', {
            method: 'POST',
            body: JSON.stringify({ username, email, password, role })
        });
        Auth.setToken(data.token);
        window.location.href = '/dashboard';
    } catch (err) {
        if (errorDiv) {
            errorDiv.textContent = err.message || 'Registration failed';
            errorDiv.style.display = 'block';
        }
    }
}

//  Add to Cart 
async function addToCart(productId, quantity = 1, size = null) {
    try {
        const body = { productId, quantity: parseInt(quantity) };
        if (size) body.size = size;
        
        await Auth.apiCall('/api/cart/add', {
            method: 'POST',
            body: JSON.stringify(body)
        });
        showToast('Added to cart!');
    } catch (err) {
        showToast(err.message || 'Failed to add to cart', 'error');
    }
}

//  Remove from Cart 
async function removeFromCart(cartItemId) {
    try {
        await Auth.apiCall(`/api/cart/remove/${cartItemId}`, { method: 'DELETE' });
        window.location.reload();
    } catch (err) {
        showToast(err.message || 'Failed to remove item', 'error');
    }
}

// Place Order 
async function placeOrder() {
    try {
        await Auth.apiCall('/api/orders/place', { method: 'POST' });
        showToast('Order placed successfully!');
        setTimeout(() => window.location.href = '/orders', 1500);
    } catch (err) {
        showToast(err.message || 'Failed to place order', 'error');
    }
}

//  Logout 
function logout() {
    window.location.href = '/logout';
}

// Toast Notification
function showToast(message, type = 'success') {
    const toast = document.createElement('div');
    toast.style.cssText = `
        position: fixed; bottom: 2rem; right: 2rem; z-index: 9999;
        padding: 1rem 1.5rem; border-radius: 12px;
        background: ${type === 'success' ? 'rgba(16, 185, 129, 0.9)' : 'rgba(239, 68, 68, 0.9)'};
        color: white; font-weight: 600; font-family: 'Inter', sans-serif;
        backdrop-filter: blur(10px); box-shadow: 0 10px 40px rgba(0,0,0,0.3);
        animation: fadeInUp 0.3s ease;
    `;
    toast.textContent = message;
    document.body.appendChild(toast);
    setTimeout(() => toast.remove(), 3000);
}
