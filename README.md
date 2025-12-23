📱 DMS Mobile (Android)
Мобильное Android-приложение для Dorm Management System (DMS).
Предназначено для студентов и администраторов общежития: авторизация, заявки, уведомления, управление проживанием.
🧱 Стек технологий
Mobile
Android (Kotlin)
Android SDK 36
Retrofit + OkHttp
MVVM
ViewBinding
Backend
Laravel
Docker + Docker Compose
Nginx + PHP-FPM
SQLite (dev)
⚠️ Важное замечание про сеть (macOS + Docker + Android Emulator)
Если вы используете macOS + Docker Desktop + Android Emulator,
10.0.2.2 может не работать из-за IPv6-проброса портов Docker.
👉 Рекомендуется использовать IPv4-адрес хоста (Mac).
🌐 Backend URL (DEV)
🔹 Получить IP хоста (macOS)
ifconfig | grep inet
Пример:
inet 10.212.3.67
🔹 Используем в Android
http://10.212.3.67:8000
🔐 Разрешение HTTP (Android 9+)
Для dev-окружения необходимо разрешить HTTP.
AndroidManifest.xml
<application
    android:usesCleartextTraffic="true"
    ... >
📦 Настройка проекта
1️⃣ Backend (Docker)
docker compose up --build
Backend будет доступен по:
http://<HOST_IP>:8000
2️⃣ Android App
В файле, где объявляется BASE_URL (например ApiClient.kt):
object ApiConfig {
    const val BASE_URL = "http://10.212.3.67:8000/"
}
🔑 Авторизация
Endpoint
POST /api/login
Пример запроса
{
  "email": "student@kbtu.kz",
  "password": "password"
}
🧪 Проверка соединения
Перед запуском приложения обязательно проверь:
В браузере Android Emulator
http://<HOST_IP>:8000/api/login
Если открывается → приложение тоже будет работать.
🚨 Частые проблемы
❌ Failed to connect to /10.0.2.2:8000
Причина: Docker Desktop пробросил порт только по IPv6
Решение: использовать IP хоста (10.xxx.xxx.xxx)
❌ Cleartext HTTP traffic not permitted
Решение: добавить android:usesCleartextTraffic="true"
📂 Структура проекта (mobile)
app/
├── data/
│   ├── api/
│   ├── model/
│   └── repository/
├── ui/
│   ├── login/
│   ├── home/
│   └── profile/
├── utils/
└── MainActivity.kt
🚀 Roadmap
 JWT refresh token
 Push-уведомления
 Offline-mode
 Role-based UI (Student / Manager)
 Dark mode
👤 Автор
Nurkhan
Full-stack developer
DMS Project (KBTU)
