# Oracle Cloud Server Cheat Sheet

This document contains all the essential commands and steps you need to manage your live deployment on Oracle Cloud.

## 1. How to Access Your Server (SSH)
To log into your cloud server from your local computer's terminal:
```bash
ssh -i /path/to/your/saved/ssh-key.key ubuntu@<YOUR_ORACLE_PUBLIC_IP>
ssh -i /path/to/your/private_key.key ubuntu@YOUR_PUBLIC_IP
```
*(Replace `/path/to/your/saved/ssh-key.key` with where you saved your private key, and `<YOUR_ORACLE_PUBLIC_IP>` with your actual server IP).*

## 2. Navigating to Your Code
Once you are logged into the server, you must navigate into your project folder before running any Docker or Git commands:
```bash
cd capsule
```
*(Note: Change `capsule` to whatever you named the folder when you ran `git clone`)*

## 3. How to Update Your Live Website
When you make changes to your code locally, follow these exact steps to push them live:

**Step A (On your local computer):**
```bash
git add .
git commit -m "Describe your changes"
git push
```

**Step B (On your Oracle server):**
```bash
git pull
sudo docker-compose up -d --build
```
*This will pull your latest code from GitHub, rebuild the Docker containers with the new code, and restart them without any downtime.*

## 4. How to Check Logs (Debugging)
If your website crashes or throws errors, you can read the live console output using these commands:

To see the **Backend (Spring Boot)** logs:
```bash
sudo docker logs capsule_backend_1
```
*(Add `-f` to watch them live: `sudo docker logs -f capsule_backend_1`)*

To see the **Frontend (React/Nginx)** logs:
```bash
sudo docker logs capsule_frontend_1
```

## 5. How to Stop or Restart the Server
To completely stop your website:
```bash
sudo docker-compose down
```

To start it back up again:
```bash
sudo docker-compose up -d
```

To forcefully restart it without rebuilding:
```bash
sudo docker-compose restart
```

## 6. How to Edit Environment Variables
If you ever need to change your database URL or API keys, you need to edit the hidden `.env` file on your server:
```bash
nano .env
```
*(Make your changes, press `Ctrl+O` then `Enter` to save, and `Ctrl+X` to exit).*
After saving, you must restart Docker for the new variables to take effect:
```bash
sudo docker-compose up -d
```
