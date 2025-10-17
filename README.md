# SkyOrbs 2.0.0 - Türkçe Minecraft Gezegen Oluşturma Eklentisi

[![Paper](https://img.shields.io/badge/Paper-1.21.4-blue.svg)](https://papermc.io/)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Maven](https://img.shields.io/badge/Maven-3.8+-red.svg)](https://maven.apache.org/)

## 📖 Açıklama

SkyOrbs, Minecraft sunucularınızda **benzersiz gezegen dünyaları** oluşturmanıza olanak tanıyan, tamamen Türkçe bir Paper eklentisidir. 17 farklı gezegen şekli, 22 biyom tipi, asteroid ve uydu sistemiyle gezegeninizi özelleştirin!

## ✨ Özellikler

### 🌍 Gezegen Sistemi
- **17+ Gezegen Şekli:** Küre, Ameba, Asteroid, Elips, Çörek, Halka, Silindir, Koni, Elmas, Küp, Piramit, Sekizyüzlü, Fraktal, Dikenli, Kuyruklu Yıldız, Hilal, Karma
- **22 Biyom Tipi:** Orman, Çöl, Buzul, Volkanik, Kristal Ormanı, Boşluk, ve daha fazlası
- **Akıllı Yerleştirme:** Gezegenler birbirine 800-2000 blok mesafede, spawn'dan minimum 1000 blok uzakta
- **1000+ Rastgele İsim:** Türkçe ve bilimkurgu tarzı benzersiz gezegen isimleri

### 🪐 Asteroid & Uydu Sistemi
- Gezegen başına 1-5 asteroid
- Gezegen başına 0-3 uydu (olasılığa dayalı)
- Tamamen config'den özelleştirilebilir

### 🎮 Komutlar (Tamamen Türkçe)
```
/gezegen create          - Yeni gezegen oluştur
/gezegen list            - Tüm gezegenleri listele
/gezegen tp <isim>       - Gezegene ışınlan
/gezegen info <isim>     - Gezegen bilgilerini göster (yakındaki gezegenlerle mesafe)
/gezegen sil <isim>      - Gezegeni sil (sadece Admin)
/gezegen reload          - Konfigürasyonu yenile (sadece Admin)
```

### ⚙️ Config-Driven Sistem
**Her şey config.yml'den düzenlenebilir:**
- Gezegen boyutları (min/max radius)
- Gezegenler arası mesafeler
- Her şekil için ağırlıklar
- Biyom dağılımları
- Asteroid/Uydu sayıları
- 1000+ isim havuzu
- Tüm mesajlar (Türkçe)

## 📦 Kurulum

### Gereksinimler
- **Minecraft:** 1.21.4
- **Server:** Paper (Spigot/Bukkit **desteklenmez**)
- **Java:** JDK 21+
- **Maven:** 3.8+ (sadece compile için)

### Adım 1: JAR Dosyasını Derle

```bash
mvn clean package
```

Derlenmiş JAR dosyası `target/SkyOrbs-2.0.0.jar` konumunda oluşacaktır.

### Adım 2: Sunucuya Yükle

1. `SkyOrbs-2.0.0.jar` dosyasını sunucunuzun `plugins/` klasörüne kopyalayın
2. Sunucuyu başlatın veya `/reload confirm` komutunu çalıştırın
3. `plugins/SkyOrbs/config.yml` dosyası otomatik oluşturulacaktır

### Adım 3: Konfigürasyonu Düzenle (İsteğe Bağlı)

`config.yml` dosyasını açıp tüm ayarları ihtiyacınıza göre değiştirin:

```yaml
# Gezegen boyutları
generation:
  planetSize:
    minRadius: 80
    maxRadius: 250

# Gezegenler arası mesafe
placement:
  clustering:
    clusterMinDistance: 800    # Minimum mesafe
    clusterMaxDistance: 2000   # Maximum mesafe
```

### Adım 4: İlk Gezegeni Oluştur

Oyuna girin ve komutu çalıştırın:

```
/gezegen create
```

## 🛠️ Geliştirme

### Proje Yapısı

```
skyorbs/
├── src/main/java/com/skyorbs/
│   ├── core/              # Temel sınıflar (Orb)
│   ├── commands/          # Komut sistemi
│   ├── generation/        # Gezegen üretimi
│   ├── shapes/            # 17 gezegen şekli
│   ├── biomes/            # 22 biyom tipi
│   ├── storage/           # SQLite veritabanı
│   ├── utils/             # Yardımcı araçlar
│   ├── config/            # Config yönetimi
│   └── features/          # Ore, Tree, Structure, Treasure
├── src/main/resources/
│   ├── config.yml         # Ultra detaylı config (1000+ isim)
│   └── plugin.yml         # Türkçe plugin tanımı
├── pom.xml
└── README.md
```

### Build

```bash
# Temizle ve derle
mvn clean package

# Sadece compile
mvn compile

# Test et (varsa)
mvn test
```

### Bağımlılıklar

- **Paper API:** 1.21.4-R0.1-SNAPSHOT
- **SQLite JDBC:** 3.44.0.0
- **Gson:** 2.10.1

## 📝 Config Ayarları

### Gezegen Boyutları

```yaml
generation:
  planetSize:
    minRadius: 80
    maxRadius: 250
    averageRadius: 150
```

### Yerleştirme

```yaml
placement:
  worldBounds:
    maxDistance: 3000              # Spawn'dan max uzaklık
    minDistanceFromSpawn: 1000     # Spawn'dan min uzaklık
  
  clustering:
    clusterMinDistance: 800        # Gezegenler arası min
    clusterMaxDistance: 2000       # Gezegenler arası max
```

### Şekil Ağırlıkları

```yaml
shapes:
  weights:
    SPHERE: 15.0
    BLOB: 12.0
    ASTEROID: 10.0
    # ... 14 şekil daha
```

### İsim Sistemi

```yaml
planetNames:
  prefixes:
    - "Zyr"
    - "Kry"
    - "Vel"
    # ... 200+ önek
  
  suffixes:
    - "ion"
    - "ara"
    - "eon"
    # ... 200+ sonek
  
  postfixes:
    - "Prime"
    - "Alpha"
    - "Beta"
    # ... 50+ takı
```

## 🎯 İzinler

```yaml
skyorbs.*           # Tüm izinler (op)
skyorbs.use         # Temel komutlar (herkes)
skyorbs.create      # Gezegen oluşturma (herkes)
skyorbs.list        # Liste görme (herkes)
skyorbs.tp          # Işınlanma (herkes)
skyorbs.info        # Bilgi görme (herkes)
skyorbs.admin       # Admin komutları (op)
```

## 🐛 Sorun Giderme

### JAR Derlenmiyor

```bash
# Java 21 kurulu mu kontrol edin
java -version

# Maven kurulu mu kontrol edin
mvn -version

# Bağımlılıkları güncelleyin
mvn clean install -U
```

### Gezegen Oluşturulmuyor

1. Konsol loglarını kontrol edin
2. `config.yml` dosyasının düzgün olduğundan emin olun
3. Veritabanı (`data.db`) dosyası erişilebilir mi kontrol edin
4. Sunucunun Paper olduğundan emin olun (Spigot/Bukkit desteklenmez)

### Performans Sorunları

`config.yml` dosyasında performans ayarlarını düzenleyin:

```yaml
performance:
  asyncGeneration: true
  threadPool:
    coreSize: 4
    maxSize: 8
  batching:
    blocksPerBatch: 500
    batchesPerTick: 5
```

## 📄 Lisans

Bu proje MIT lisansı altında lisanslanmıştır.

## 🤝 Katkıda Bulunma

1. Fork edin
2. Feature branch oluşturun (`git checkout -b feature/AmazingFeature`)
3. Commit edin (`git commit -m 'Add some AmazingFeature'`)
4. Push edin (`git push origin feature/AmazingFeature`)
5. Pull Request açın

## 📧 İletişim

Sorularınız için Issue açabilirsiniz.

## 🙏 Teşekkürler

- Paper ekibine harika API için
- Minecraft topluluğuna ilham için
- Türkçe plugin ekosistemini destekleyen herkese

---

**SkyOrbs 2.0.0** - Uzayda kendi gezegeni ni oluştur! 🌍✨
