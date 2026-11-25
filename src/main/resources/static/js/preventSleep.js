/**
 * preventSleep.js - Heartbeat ping mechanism
 * 
 * Automatically keeps cloud services (such as Render / Koyeb) active while
 * any tab or page of the Census Management System is open in the user's browser.
 * Sends a lightweight HTTP GET ping every 40 seconds to prevent inactivity spin-downs.
 */
(function() {
    'use strict';

    // Interval: 40 seconds (well before Render's inactivity timeout)
    const PING_INTERVAL_MS = 40 * 1000;
    const PING_ENDPOINT = '/api/ping';

    function sendHeartbeat() {
        fetch(PING_ENDPOINT, {
            method: 'GET',
            cache: 'no-store',
            headers: { 'Accept': 'application/json' }
        })
        .then(response => {
            if (response.ok) {
                const now = new Date().toLocaleTimeString();
                console.log(`%c[PreventSleep] Active Heartbeat sent at ${now} (Server Awake)`, 'color: #10b981; font-weight: bold;');
            }
        })
        .catch(err => {
            console.debug('[PreventSleep] Ping attempt:', err.message);
        });
    }

    // Send first ping immediately after page loads
    if (document.readyState === 'complete' || document.readyState === 'interactive') {
        sendHeartbeat();
    } else {
        window.addEventListener('DOMContentLoaded', sendHeartbeat);
    }

    // Schedule regular heartbeat every 40 seconds
    setInterval(sendHeartbeat, PING_INTERVAL_MS);

    // Also send an immediate ping when user refocuses or switches back to tab
    document.addEventListener('visibilitychange', function() {
        if (!document.hidden) {
            sendHeartbeat();
        }
    });
})();
