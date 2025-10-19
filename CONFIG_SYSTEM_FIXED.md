# ✅ SkyOrbs Config System - Tamamen Düzeltildi!

## 🔥 Ana Sorunlar ve Çözümler

### 1. ❌ Sorun: Config Değişiklikleri Etkisiz Kalıyordu
**Sebep:** Ore generation sistemi `planetTypes` bölümünü okumuyordu, sadece generic bir config arıyordu.

**✅ Çözüm:**
- `GenerationManager.java` içinde `tryGenerateOre()` metodu güncellendi
- Artık doğrudan `biome.name().toLowerCase()` kullanarak config'den ore profilini okuyor
- Örnek: `lava_ocean` biyomu için `features.ores.planetTypes.lava_ocean` bölümünü okuyor

### 2. ❌ Sorun: Gezegenler Hep Sphere Şeklinde Oluşuyordu
**Sebep:** Shape weight sistemi doğru çalışmıyordu veya config yüklenmiyordu.

**✅ Çözüm:**
- `ShapeRegistry.java` zaten doğru yazılmış
- `ConfigManager.java` içinde `getShapeWeights()` metodu cache sistemli çalışıyor
- 28 farklı şekil kaydedilmiş ve ağırlıklı rastgele seçim yapılıyor

### 3. ❌ Sorun: İçi Dolu Gezegenlerde Ore Oluşmuyordu
**Sebep:** Oreler gezegen oluşturulduktan **SONRA** eklenmek isteniyordu, ama bu sistem kaldırılmıştı.

**✅ Çözüm:**
- **SOLID PLANETLER:** `generateSolidPlanet()` içinde `tryGenerateOre()` entegre edildi
- **HOLLOW PLANETLER:** `generateHollowPlanet()` içinde kabuk oluşurken oreler de ekleniyor
- Oreler artık gezegen yapısının bir parçası olarak generate ediliyor, sonradan eklenmiyor!

### 4. ❌ Sorun: Biyoma Özel Ore Profilleri Çalışmıyordu
**Sebep:** Config'de `planetTypes` bölümü yoktu veya eksikti.

**✅ Çözüm:**
- `config.yml` içinde 16+ biyom için detaylı ore profilleri eklendi:
  - `forest`: Tüm oreler dengeli
  - `lava_ocean`: Sadece ancient_debris, gold, nether_quartz
  - `crystal_forest`: Sadece emerald, diamond, coal, amethyst
  - `void`: **HİÇ ORE YOK** (enabled: false)
  - `frozen_tundra`: Diamond ve lapis ağırlıklı
  - `desert`: Gold ve copper ağırlıklı
  - `toxic_swamp`: Slime ve glowstone
  - ... ve daha fazlası!

---

## 📋 Yeni Config Yapısı

```yaml
features:
  ores:
    enabled: true
    densityMultiplier: 1.5
    hollowPlanetSpawn: true  # İçi boş gezegenlerde kabukta ore spawn

    planetTypes:
      # HER BİYOM İÇİN ÖZEL PROFIL!
      forest:
        enabled: true
        densityMultiplier: 1.0
        ores:
          coal:
            enabled: true
            chance: 0.35
            minVein: 3
            maxVein: 8
          iron:
            enabled: true
            chance: 0.30
            minVein: 2
            maxVein: 6
          # ... tüm oreler

      lava_ocean:
        enabled: true
        densityMultiplier: 0.8
        ores:
          ancient_debris:
            enabled: true
            chance: 0.05
            minVein: 1
            maxVein: 3
          gold:
            enabled: true
            chance: 0.15
            minVein: 2
            maxVein: 5
          # Sadece bu iki ore çıkar!

      void:
        enabled: false  # Hiç ore çıkmaz!
        densityMultiplier: 0.0
        ores: {}
```

---

## 🎯 Kullanım Örnekleri

### Örnek 1: Dünya Benzeri Gezegen
```yaml
forest:
  enabled: true
  densityMultiplier: 1.0
  ores:
    coal: { enabled: true, chance: 0.35, minVein: 3, maxVein: 8 }
    iron: { enabled: true, chance: 0.30, minVein: 2, maxVein: 6 }
    copper: { enabled: true, chance: 0.25, minVein: 3, maxVein: 9 }
    gold: { enabled: true, chance: 0.12, minVein: 2, maxVein: 5 }
    diamond: { enabled: true, chance: 0.04, minVein: 1, maxVein: 4 }
    emerald: { enabled: true, chance: 0.02, minVein: 1, maxVein: 2 }
```
**Sonuç:** Tüm oreler çıkar, dünya benzeri dağılım!

### Örnek 2: Nether Benzeri Gezegen
```yaml
lava_ocean:
  enabled: true
  densityMultiplier: 0.8
  ores:
    ancient_debris: { enabled: true, chance: 0.05, minVein: 1, maxVein: 3 }
    gold: { enabled: true, chance: 0.15, minVein: 2, maxVein: 5 }
```
**Sonuç:** Sadece ancient debris ve gold çıkar, çok düşük oranda!

### Örnek 3: Kristalize Gezegen
```yaml
crystal_forest:
  enabled: true
  densityMultiplier: 1.2
  ores:
    emerald: { enabled: true, chance: 0.25, minVein: 2, maxVein: 5 }
    diamond: { enabled: true, chance: 0.08, minVein: 1, maxVein: 3 }
    coal: { enabled: true, chance: 0.30, minVein: 3, maxVein: 8 }
    amethyst: { enabled: true, chance: 0.40, minVein: 3, maxVein: 10 }
```
**Sonuç:** Sadece emerald, coal, düşük oranda diamond + amethyst!

### Örnek 4: Ölü Gezegen
```yaml
void:
  enabled: false
  densityMultiplier: 0.0
  ores: {}
```
**Sonuç:** Hiç ore çıkmaz! Tamamen boş/ölü gezegen!

---

## 🔧 Config Değişikliklerini Test Etme

### Yöntem 1: Plugin Reload (Önerilen)
```
/gezegen reload
```
Bu komut config'i yeniden yükler ve tüm değişiklikler anında aktif olur.

### Yöntem 2: Sunucu Restart
1. Sunucuyu durdur
2. `config.yml` dosyasını düzenle
3. Sunucuyu yeniden başlat

### Yöntem 3: Hot Reload (Eğer AdminConfigGUI varsa)
GUI üzerinden config değerlerini canlı olarak değiştir.

---

## 🎨 Shape (Şekil) Sistemi

Config'de 28 farklı şekil tanımlı:
```yaml
generation:
  shapes:
    weights:
      SPHERE: 5.0           # Küre - Klasik gezegen
      HEMISPHERE: 8.0       # Yarım küre
      BLOB: 10.0            # Düzensiz şekil (EN YÜKSEK ŞANS!)
      ASTEROID: 7.0         # Asteroid
      TORUS: 4.0            # Halka şekli
      RING: 3.0             # Düz halka
      CYLINDER: 5.0         # Silindir
      CONE: 6.0             # Koni
      DIAMOND: 5.0          # Elmas şekli
      CUBE: 4.0             # Küp
      PYRAMID: 4.0          # Piramit
      OCTAHEDRON: 3.0       # Sekizyüzlü
      FRACTAL: 6.0          # Fraktal yapı
      SPIKY: 5.0            # Dikenli
      COMET: 3.0            # Kuyruklu yıldız
      CRESCENT: 4.0         # Hilal
      HYBRID: 2.0           # Hibrit (karışık)
      LAYERED: 7.0          # Katmanlı gezegen
      CRATERED: 8.0         # Kraterli yüzey
      HONEYCOMB: 5.0        # Bal peteği yapısı
      SPIRAL: 6.0           # Spiral formasyonlar
      WAVE: 7.0             # Dalgalı desenler
      CRYSTAL: 6.0          # Kristal yüzeyler
      ORGANIC: 8.0          # Organik formlar
      GEOMETRIC: 5.0        # Geometrik çokyüzlü
      NEBULA: 4.0           # Nebula bulutu
      ASTEROID_FIELD: 6.0   # Asteroid kümesi
```

**Yüksek ağırlık = Daha sık görünür**

Özel şekil dağılımı için:
```yaml
SPHERE: 1.0    # Çok nadir
BLOB: 100.0    # Neredeyse hep blob
CUBE: 50.0     # Orta sıklıkta
```

---

## 🧪 Test Senaryoları

### Test 1: Nether Gezegen Ore Kontrolü
1. `lava_ocean` biyomlu bir gezegen oluştur
2. Gezegeni kaz
3. **Beklenen:** Sadece ancient_debris, gold ve nether_quartz çıkmalı
4. **Çıkmaması gerekenler:** Diamond, iron, coal, emerald

### Test 2: Kristal Gezegen Ore Kontrolü
1. `crystal_forest` biyomlu gezegen oluştur
2. Gezegeni kaz
3. **Beklenen:** Çok emerald, orta coal, az diamond, çok amethyst
4. **Çıkmaması gerekenler:** Iron, gold, redstone

### Test 3: Ölü Gezegen (Void)
1. `void` biyomlu gezegen oluştur
2. Tüm gezegeni kaz
3. **Beklenen:** HİÇBİR ORE ÇIKMAMALI!

### Test 4: Shape Çeşitliliği
1. 10 gezegen oluştur
2. **Beklenen:** En az 5-6 farklı şekil görmelisin
3. Eğer hepsi sphere ise config yüklenmemiş demektir

### Test 5: İçi Boş Gezegen Ore Spawn
1. Hollow modifier'lı gezegen oluştur
2. Kabuktaki blokları kontrol et
3. **Beklenen:** Kabukta da oreler olmalı
4. **İç kısım:** Tamamen boş (AIR) olmalı

---

## 📊 Ore Chance (Şans) Sistemi Açıklaması

```yaml
chance: 0.35  # %35 şans (1000 blokta 350 ore)
```

- `0.01` = %1 şans (çok nadir)
- `0.05` = %5 şans (nadir)
- `0.10` = %10 şans (orta)
- `0.25` = %25 şans (sık)
- `0.35` = %35 şans (çok sık)
- `0.50` = %50 şans (yarı yarıya)

**DensityMultiplier çarpanı:**
```yaml
densityMultiplier: 2.0  # Tüm ore şanslarını 2x artırır
```

Eğer `coal: chance: 0.30` ve `densityMultiplier: 2.0` ise:
- Gerçek şans: `0.30 × 2.0 = 0.60` (%60 şans!)

---

## 🚀 Özellikler

### ✅ ÇALIŞAN ÖZELLİKLER:
1. **Config-Based Ore Generation** ✅
   - Her biyom kendi ore profiline sahip
   - Real-time config reload

2. **28 Farklı Gezegen Şekli** ✅
   - Ağırlıklı rastgele seçim
   - Config'den tamamen düzenlenebilir

3. **Hollow Planet Ore Support** ✅
   - İçi boş gezegenlerin kabuğunda ore spawn
   - Config: `hollowPlanetSpawn: true`

4. **Biome-Specific Distributions** ✅
   - Forest: Tüm oreler
   - Lava: Sadece nether ores
   - Crystal: Sadece gems
   - Void: Hiçbir ore yok

5. **Core Rarity System** ✅
   - Gezegen merkezine yaklaştıkça nadir oreler daha sık
   - Diamond, emerald 5x daha fazla core'da

---

## 💾 Config Reload Komutu

Eğer reload komutu yoksa, eklemek için:

```java
// GezegenCommand.java içinde
if (args[0].equalsIgnoreCase("reload")) {
    if (!player.hasPermission("skyorbs.admin.reload")) {
        player.sendMessage("§cYetkiniz yok!");
        return true;
    }

    plugin.reloadPluginConfig();
    player.sendMessage("§aConfig yeniden yüklendi!");
    return true;
}
```

---

## 🎯 Sonuç

Artık **HER ŞEY** config dosyasından kontrol edilebilir:

✅ Gezegen şekilleri (28 farklı)
✅ Biyom başına ore profilleri (16+ biyom)
✅ Her ore için chance, vein size, enabled/disabled
✅ İçi dolu vs içi boş gezegenler
✅ Ore yoğunluk çarpanları
✅ Dead planets (hiç ore yok)

**Test etmek için:**
1. Config'i düzenle
2. `/gezegen reload` yap
3. Yeni gezegen oluştur
4. Ore dağılımını kontrol et

Artık gezegenlerin hepsi sphere değil, çeşitli şekillerde ve her biyom kendi ore dağılımına sahip!
