# SkyOrbs - Minecraft Plugin Projesi

## Proje Özeti
SkyOrbs 2.0.0 - Türkçe Minecraft gezegen oluşturma eklentisi
- **Platform:** Paper 1.21.4
- **Java:** 21
- **Build System:** Maven
- **Dosya Sayısı:** 127 Java dosyası (~16,700 satır kod)

## Proje Yapısı
```
src/main/java/com/skyorbs/
├── core/          - Temel sınıflar (Orb, PlanetType, PlanetEvolution)
├── commands/      - Komut sistemi (/gezegen)
├── config/        - ConfigManager
├── generation/    - Gezegen üretim sistemi
├── shapes/        - 70+ gezegen şekli
├── biomes/        - 22 biyom tipi
├── storage/       - SQLite veritabanı + PlanetDataManager
├── atmosphere/    - Atmosfer efekt sistemi
├── ecosystem/     - Biyosfer yönetimi
├── events/        - Gök olayları
├── features/      - Ore, Tree, Structure, Treasure, Asteroid
├── dungeons/      - Zindan üretimi
├── gui/           - Admin GUI
├── listeners/     - Event listeners
├── modifiers/     - Gezegen modifikatörleri
├── palettes/      - Renk paletleri
└── utils/         - Yardımcı araçlar
```

## Özellikler
- ✅ 70+ farklı gezegen şekli (Sphere, Blob, Star, Rainbow, Butterfly, vb.)
- ✅ 22 biyom tipi
- ✅ Atmosfer efekt sistemi
- ✅ SQLite veritabanı entegrasyonu
- ✅ Asteroid & Uydu sistemi
- ✅ Zindan, hazine, yapı sistemi
- ✅ Config-driven (ultra detaylı config.yml)
- ✅ Async gezegen üretimi
- ✅ BossBar/ActionBar progress gösterimi
- ✅ Admin GUI sistemi

## Build Komutu
```bash
mvn clean package
```

JAR dosyası: `target/SkyOrbs-2.0.0.jar`

## Test Coverage
- ShapeRegistryTest.java (33 şekil testi)
- ConfigManagerTest.java (config doğrulama)
- BiomeTypeTest.java (biyom testleri)

## Son Değişiklikler
- 2025-01-19: Replit environment setup başlatıldı
- Java 21 ve Maven kurulumu yapılacak
- Build sistemi yapılandırılacak
