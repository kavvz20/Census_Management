/**
 * preventSleep.js - Cloud Server Keep-Alive Utility
 * 
 * Keeps your free cloud hosting (Render, Koyeb, etc.) active and prevents spin-down.
 * 
 * Run with:
 *   node preventSleep.js [YOUR_APP_URL]
 * 
 * Example:
 *   node preventSleep.js https://census-management-system.onrender.com
 * 
 * Note: When anyone opens the website in their browser, the web app's built-in
 * client heartbeat (/js/preventSleep.js) already sends automatic pings every 40s!
 */

const https = require('https');
const http = require('http');

const targetUrl = process.argv[2] || process.env.APP_URL || 'http://localhost:8080';
const pingUrl = targetUrl.replace(/\/+$/, '') + '/api/ping';
const INTERVAL_SECONDS = 45;

console.log('================================================================');
console.log('            PREVENT SLEEP - CLOUD KEEP-ALIVE PINGER             ');
console.log('================================================================');
console.log(`Target Ping URL : ${pingUrl}`);
console.log(`Interval        : Every ${INTERVAL_SECONDS} seconds`);
console.log('Press Ctrl + C to stop at any time.');
console.log('----------------------------------------------------------------');

function pingServer() {
    const client = pingUrl.startsWith('https') ? https : http;
    const req = client.get(pingUrl, (res) => {
        const time = new Date().toLocaleTimeString();
        if (res.statusCode >= 200 && res.statusCode < 300) {
            console.log(`[${time}] [OK] Server awake and responsive (HTTP ${res.statusCode})`);
        } else {
            console.log(`[${time}] [WARN] Server responded with HTTP ${res.statusCode}`);
        }
        res.resume(); // Consume response data to free up memory
    });

    req.on('error', (err) => {
        const time = new Date().toLocaleTimeString();
        console.error(`[${time}] [FAIL] Ping failed: ${err.message}`);
    });

    req.setTimeout(10000, () => {
        req.destroy();
        const time = new Date().toLocaleTimeString();
        console.warn(`[${time}] [TIMEOUT] Request timed out`);
    });
}

// Initial ping
pingServer();

// Continuous ping every 45 seconds
setInterval(pingServer, INTERVAL_SECONDS * 1000);
