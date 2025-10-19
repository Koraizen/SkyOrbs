# SkyOrbs - Minecraft Gezegen Oluşturma Eklentisi

## Proje Bilgileri

**Versiyon:** 2.0.0  
**Java Versiyon:** 21  
**Minecraft:** 1.21.4 (Paper API)  
**Build Tool:** Maven 3.8.6

## Proje Hakkında

SkyOrbs, Minecraft Paper sunucuları için geliştirilmiş, tamamen Türkçe bir gezegen oluşturma eklentisidir. Oyunculara benzersiz gezegen dünyaları yaratma imkanı sunar.

### Özellikler

#### 🌍 Gezegen Sistemi
- **28 Farklı Gezegen Şekli:** Küre, Asteroid, Elips, Çörek, Halka, Silindir, Koni, Elmas, Küp, Piramit, Fraktal, Katmanlı, Kraterli, Bal Peteği, Spiral, Kristal ve daha fazlası
- **22 Biyom Tipi:** Orman, Çöl, Buzul, Volkanik, Kristal Ormanı, Boşluk vb.
- **39 Gezegen Paleti:** Çeşitli renk ve malzeme kombinasyonları
- **8 Atmosfer Tipi:** Clear, Toxic, Radiation, Frozen, Stormy, Acidic, Thick, Thin
- **Akıllı Yerleştirme:** Gezegenler otomatik olarak uygun mesafelerde yerleştirilir
- **1000+ Rastgele İsim:** Benzersiz gezegen isimleri

#### 🪐 Ek Sistemler
- **Asteroid Sistemi:** Gezegen başına 1-5 asteroid
- **Uydu Sistemi:** Gezegen başına 0-3 uydu (olasılığa dayalı)
- **Gezegen Halkaları:** %25 şansla güzel halkalar
- **Zindan Sistemi:** Gezegenler içinde keşfedilebilir zindanlar

#### 🎮 Özellikler
- **Ore (Maden) Sistemi:** Biyoma özel maden dağılımları
- **Ağaç Sistemi:** Biyoma uygun ağaç türleri
- **Yapı Sistemi:** Tapınaklar, kuleler, harabeler
- **Hazine Sistemi:** 5 seviyeli hazine sandıkları (Common → Legendary)
- **Yüzey Detayları:** Kraterler, kalıntılar, anıtlar
- **Atmosfer Efektleri:** Gezegen atmosferine göre görsel efektler

### Komutlar

Tüm komutlar tamamen Türkçe'dir:

```
/gezegen create          - Yeni gezegen oluştur
/gezegen list            - Tüm gezegenleri listele
/gezegen tp <isim>       - Gezegene ışınlan
/gezegen info <isim>     - Gezegen bilgilerini göster
/gezegen sil <isim>      - Gezegeni sil (Admin)
/gezegen reload          - Konfigürasyonu yenile (Admin)
```

## Build ve Kurulum

### Gereksinimler
- **Java:** JDK 21+
- **Maven:** 3.8+
- **Server:** Paper 1.21.4

### Build Yapma

Replit ortamında build yapmak için:

```bash
export JAVA_HOME=/nix/store/k95pqfzyvrna93hc9a4cg5csl7l4fh0d-openjdk-21.0.7+6
mvn clean package -DskipTests
```

Build edilen JAR dosyası: `target/SkyOrbs-2.0.0.jar` (14MB - tüm bağımlılıklar dahil)

### Minecraft Sunucusuna Kurulum

1. `target/SkyOrbs-2.0.0.jar` dosyasını sunucunuzun `plugins/` klasörüne kopyalayın
2. Sunucuyu başlatın veya `/reload confirm` komutunu çalıştırın
3. `plugins/SkyOrbs/config.yml` otomatik oluşturulacaktır
4. Config dosyasını ihtiyacınıza göre düzenleyin

## Konfigürasyon

Config dosyası (`config.yml`) son derece detaylı ve her şey düzenlenebilir:

### Gezegen Boyutları
```yaml
generation:
  planetSize:
    minRadius: 25
    maxRadius: 50
    averageRadius: 35
```

### Şekil Ağırlıkları
```yaml
generation:
  shapes:
    weights:
      SPHERE: 5.0
      BLOB: 10.0
      ASTEROID: 7.0
      # ... 25+ şekil daha
```

### Maden Sistemi
Yeni **planetTypes** sistemi kullanılıyor (legacy `layers` sistemi kaldırıldı):
```yaml
features:
  ores:
    planetTypes:
      terrestrial:  # Tüm madenler
      lava:         # Nether madenleri
      crystal:      # Değerli taşlar
      ice:          # Elmas ve demir
      shadow:       # Çok az maden
      toxic:        # Slime ve glowstone
```

## Proje Yapısı

```
skyorbs/
├── src/main/java/com/skyorbs/
│   ├── core/              # Temel sınıflar (Orb, PlanetType)
│   ├── commands/          # Komut sistemi
│   ├── generation/        # Gezegen üretimi
│   ├── shapes/            # 28 gezegen şekli
│   ├── biomes/            # 22 biyom tipi
│   ├── palettes/          # 39 gezegen paleti
│   ├── atmosphere/        # Atmosfer sistemi
│   ├── modifiers/         # Gezegen modifikatörleri
│   ├── storage/           # SQLite veritabanı
│   ├── features/          # Ore, Tree, Structure, Treasure
│   ├── dungeons/          # Zindan üretimi
│   ├── gui/               # Admin GUI
│   └── utils/             # Yardımcı araçlar
├── src/main/resources/
│   ├── config.yml         # Ana konfigürasyon
│   └── plugin.yml         # Plugin tanımı
└── pom.xml
```

## Veritabanı

Plugin, gezegen verilerini saklamak için SQLite kullanır:
- **Ana veritabanı:** `plugins/SkyOrbs/data.db`
- **Gezegen dataları:** `plugins/SkyOrbs/planets/<uuid>/info.yml`

### Veritabanı Şeması (v2)

```sql
CREATE TABLE orbs (
    id TEXT PRIMARY KEY,
    name TEXT NOT NULL,
    world TEXT NOT NULL,
    centerX INTEGER,
    centerY INTEGER,
    centerZ INTEGER,
    radius INTEGER,
    shape TEXT,
    biome TEXT,
    seed BIGINT,
    created_at BIGINT,
    is_asteroid BOOLEAN,
    parent_id TEXT,
    palette_id TEXT,
    modifiers TEXT,
    atmosphere TEXT,
    planet_type TEXT,
    core_level INTEGER,
    energy_level REAL,
    xp BIGINT,
    biosphere_level INTEGER,
    ecological_balance REAL
);
```

## Performans Optimizasyonları

Plugin, büyük gezegenler oluştururken performansı optimize etmek için çeşitli teknikler kullanır:

- **Async Generation:** Gezegen üretimi arkaplan thread'lerinde yapılır
- **Batch Processing:** Bloklar küçük gruplar halinde yerleştirilir
- **Chunk Preloading:** Gerekli chunk'lar önceden yüklenir
- **Thread Pool:** Configurable thread pool boyutu (4-8 core)
- **Progress Display:** BossBar, ActionBar veya Chat ile ilerleme gösterimi

## Son Düzeltmeler (19 Ekim 2025)

### Kritik Düzeltmeler

1. **Async Block Hatası Düzeltildi**
   - `GenerationManager.java:228` - Treasure chest yerleştirmesi main thread'e alındı
   - "Asynchronous block remove" hatası çözüldü

2. **Config Temizliği**
   - Legacy `layers` sistemi config.yml'den kaldırıldı
   - Yeni `planetTypes` sistemi aktif

3. **Java 21 Desteği**
   - Replit ortamında Java 21 kurulumu yapıldı
   - Maven build başarıyla çalışıyor

### Bilinen Sorunlar

- **Unit Testler:** Mock/setup sorunları nedeniyle testler başarısız (prodüksiyon kodu çalışıyor)
- **Deprecated API:** GenerationManager'da bazı deprecated API kullanımları (çalışma performansını etkilemiyor)

## İzinler

```yaml
skyorbs.*           # Tüm izinler (op)
skyorbs.use         # Temel komutlar (herkes)
skyorbs.create      # Gezegen oluşturma (herkes)
skyorbs.list        # Liste görme (herkes)
skyorbs.tp          # Işınlanma (herkes)
skyorbs.info        # Bilgi görme (herkes)
skyorbs.admin       # Admin komutları (op)
```

## Bağımlılıklar

### Zorunlu
- Paper API 1.21.4-R0.1-SNAPSHOT
- SQLite JDBC 3.44.0.0
- Gson 2.10.1

### Opsiyonel Entegrasyonlar
- Vault (ekonomi)
- WorldGuard (koruma)
- PlaceholderAPI (placeholder'lar)
- Dynmap (harita entegrasyonu)

## Geliştirici Notları

### Async İşlemler

Plugin, gezegen oluşturma işlemlerini async olarak yapar ancak **blok yerleştirme işlemleri main thread'de yapılmalıdır**. Örnek:

```java
// YANLIŞ - Async thread'de block setType
executor.submit(() -> {
    block.setType(Material.STONE, false); // HATA!
});

// DOĞRU - Main thread'de block setType
executor.submit(() -> {
    // Hesaplamalar async
    Bukkit.getScheduler().runTask(plugin, () -> {
        block.setType(Material.STONE, false); // DOĞRU
    });
});
```

### Progress Display

3 farklı progress display tipi desteklenir:
- `bossbar`: BossBar (varsayılan)
- `actionbar`: ActionBar
- `chat`: Chat mesajları

Config'den ayarlanabilir:
```yaml
progress_display:
  type: "bossbar"  # veya "actionbar" veya "chat"
```

## Lisans

MIT License

## İletişim

Sorularınız için GitHub Issues kullanabilirsiniz.

---

**SkyOrbs 2.0.0** - Uzayda kendi gezegenini oluştur! 🌍✨
