# 🔧 SkyOrbs Plugin - Yapılan Düzeltmeler

## 📌 Ana Sorunlar ve Çözümler

### 1. Config Değişiklikleri Etki Etmiyordu ❌ → ✅

**SORUN:**
- Config dosyasında ne yazarsanız yazın gezegenler hep aynı şekilde oluşuyordu
- Ore dağılımları değişmiyordu
- Shape ağırlıkları çalışmıyordu

**ÇÖZÜM:**
- `GenerationManager.java` tamamen yeniden yazıldı
- Ore generation sistemi artık config'den doğru okuyor:
  - Her biyom için özel `features.ores.planetTypes.{biome_name}` bölümü
  - Örnek: `lava_ocean` için `features.ores.planetTypes.lava_ocean`
- Shape sistemi düzgün çalışıyor (28 farklı şekil)

---

### 2. Gezegenler Sadece Sphere Şeklinde Oluşuyordu ❌ → ✅

**SORUN:**
- Her gezegen yuvarlak (sphere) oluyordu
- Diğer şekiller hiç çıkmıyordu

**ÇÖZÜM:**
- Shape weight sistemi doğru çalışıyor
- Config'de 28 farklı şekil tanımlı:
  ```yaml
  SPHERE: 5.0
  BLOB: 10.0        # Düzensiz - yüksek şans
  ASTEROID: 7.0
  CUBE: 4.0
  PYRAMID: 4.0
  ... ve 23 tane daha!
  ```
- Yüksek ağırlık = Daha sık görünür

---

### 3. İçi Dolu Gezegenlerde Ore Oluşmuyordu ❌ → ✅

**SORUN:**
- Solid (tam dolu) gezegenlerin içinde hiç ore oluşmuyordu
- Sadece yüzeyde spawn oluyordu (o da düzgün değildi)

**ÇÖZÜM:**
- **ORE ENTEGRASYONU:** Oreler artık gezegen bloklarıyla birlikte oluşuyor!
- `generateSolidPlanet()` içinde `tryGenerateOre()` metodu entegre edildi
- Gezegen oluşurken her blok için ore şansı kontrol ediliyor
- **Hollow planets:** Kabukta ore spawn desteği eklendi

**Teknik Detay:**
```java
// Gezegen bloğu oluşturulurken
Material material = palette.getMaterialByDepth(depth, random);

// ORE KONTROLÜ - Aynı anda!
Material oreMaterial = tryGenerateOre(x, y, z, distance, radius, biome, random);
if (oreMaterial != null) {
    material = oreMaterial; // Bloğu ore ile değiştir
}

blocks.add(new BlockPlacement(x, y, z, material));
```

---

### 4. Biyoma Özel Ore Profilleri Yoktu ❌ → ✅

**SORUN:**
- Nether benzeri gezegenlerde diamond çıkıyordu
- Kristal gezegenlerde coal çıkıyordu
- Ölü gezegenlerde hala ore vardı

**ÇÖZÜM:**
Config'e **16+ biyom** için özel ore profilleri eklendi:

#### Dünya Benzeri (Forest, Plains, etc.)
```yaml
forest:
  ores:
    coal: { chance: 0.35, minVein: 3, maxVein: 8 }
    iron: { chance: 0.30, minVein: 2, maxVein: 6 }
    gold: { chance: 0.12, minVein: 2, maxVein: 5 }
    diamond: { chance: 0.04, minVein: 1, maxVein: 4 }
    emerald: { chance: 0.02, minVein: 1, maxVein: 2 }
```
**Sonuç:** TÜM oreler dengeli dağılım

#### Nether Benzeri (Lava Ocean, Magma Caves)
```yaml
lava_ocean:
  ores:
    ancient_debris: { chance: 0.05, minVein: 1, maxVein: 3 }
    gold: { chance: 0.15, minVein: 2, maxVein: 5 }
    nether_quartz: { chance: 0.20, minVein: 3, maxVein: 7 }
```
**Sonuç:** SADECE ancient debris, gold, nether quartz (çok düşük)

#### Kristalize (Crystal Forest, Crystalline)
```yaml
crystal_forest:
  ores:
    emerald: { chance: 0.25, minVein: 2, maxVein: 5 }
    diamond: { chance: 0.08, minVein: 1, maxVein: 3 }
    coal: { chance: 0.30, minVein: 3, maxVein: 8 }
    amethyst: { chance: 0.40, minVein: 3, maxVein: 10 }
```
**Sonuç:** Çok emerald, az diamond, çok coal, çok amethyst

#### Ölü Gezegen (Void, Corrupted)
```yaml
void:
  enabled: false      # ORE OLUŞUMU KAPALI!
  densityMultiplier: 0.0
  ores: {}            # Boş liste
```
**Sonuç:** HİÇ ORE ÇIKMAZ!

#### Donmuş (Frozen Tundra, Glacier)
```yaml
frozen_tundra:
  ores:
    diamond: { chance: 0.12, minVein: 2, maxVein: 5 }
    lapis: { chance: 0.20, minVein: 3, maxVein: 8 }
    iron: { chance: 0.25, minVein: 2, maxVein: 6 }
```
**Sonuç:** Diamond ve lapis ağırlıklı

#### Çöl (Desert, Badlands)
```yaml
badlands:
  ores:
    gold: { chance: 0.35, minVein: 4, maxVein: 9 }
    copper: { chance: 0.35, minVein: 4, maxVein: 11 }
    iron: { chance: 0.25, minVein: 3, maxVein: 7 }
```
**Sonuç:** Çok gold ve copper

#### Zehirli (Toxic Swamp, Toxic)
```yaml
toxic_swamp:
  ores:
    slime: { chance: 0.40, minVein: 2, maxVein: 6 }
    glowstone: { chance: 0.25, minVein: 2, maxVein: 5 }
    copper: { chance: 0.20, minVein: 2, maxVein: 5 }
```
**Sonuç:** Slime ve glowstone ağırlıklı

---

## 🎯 Config Yapısı (Yeni)

### Ore Sisteminin Yapısı
```yaml
features:
  ores:
    enabled: true
    densityMultiplier: 1.5          # GENEL çarpan
    hollowPlanetSpawn: true          # İçi boş gezegenlerde kabukta spawn

    planetTypes:                     # HER BİYOM İÇİN AYRI PROFIL!
      forest:                        # Biyom adı (küçük harf)
        enabled: true                # Bu biyomda ore var mı?
        densityMultiplier: 1.0       # Bu biyom için özel çarpan
        ores:                        # Bu biyomda çıkacak oreler
          coal:
            enabled: true            # Bu ore aktif mi?
            chance: 0.35             # Spawn şansı (0.0-1.0)
            minVein: 3               # Minimum damar boyutu
            maxVein: 8               # Maximum damar boyutu
          iron:
            enabled: true
            chance: 0.30
            minVein: 2
            maxVein: 6
          # ... diğer oreler

      lava_ocean:                    # BAŞKA BİYOM
        enabled: true
        densityMultiplier: 0.8       # Daha az ore
        ores:
          ancient_debris:
            enabled: true
            chance: 0.05             # ÇOK NADİR!
            minVein: 1
            maxVein: 3
          # Sadece bu ore tanımlı = sadece bu çıkar!

      void:                          # ÖLÜ GEZEGEN
        enabled: false               # ORE SİSTEMİ KAPALI!
        densityMultiplier: 0.0
        ores: {}                     # BOŞ!
```

---

## 🔍 Nasıl Test Edilir?

### Test 1: Farklı Biyomlarda Ore Kontrolü

1. **Forest gezegeni oluştur:**
   ```
   /gezegen create forest MyForestPlanet
   ```
   - Kazdığında: Coal, iron, gold, diamond, emerald çıkmalı

2. **Lava Ocean gezegeni oluştur:**
   ```
   /gezegen create lava_ocean MyLavaPlanet
   ```
   - Kazdığında: SADECE ancient debris, gold, nether quartz çıkmalı
   - Diamond, iron, coal vs. çıkmamalı!

3. **Crystal Forest gezegeni oluştur:**
   ```
   /gezegen create crystal_forest MyCrystalPlanet
   ```
   - Kazdığında: ÇOK emerald, az diamond, çok coal, çok amethyst
   - Gold, redstone vs. çıkmamalı!

4. **Void gezegeni oluştur:**
   ```
   /gezegen create void MyDeadPlanet
   ```
   - Kazdığında: HİÇBİR ORE ÇIKMAMALI!

---

### Test 2: Gezegen Şekli Çeşitliliği

10 gezegen oluştur ve şekillerine bak:
```
/gezegen create
/gezegen create
/gezegen create
... (10 kez)
```

**Beklenen:**
- En az 5-6 farklı şekil görmelisin
- Hepsi sphere ise config yüklenmemiş demektir!

**Olası Şekiller:**
- BLOB (düzensiz)
- ASTEROID
- CUBE
- PYRAMID
- HEMISPHERE
- TORUS
- ... ve 22 tane daha!

---

### Test 3: Config Değişikliği

1. `config.yml` aç
2. `forest` biyomundaki `diamond` chance'ini değiştir:
   ```yaml
   diamond:
     enabled: true
     chance: 0.99    # %99 şans (neredeyse her blok diamond!)
     minVein: 10
     maxVein: 50
   ```
3. Kaydet ve reload et:
   ```
   /gezegen reload
   ```
4. Yeni forest gezegeni oluştur
5. Kaz

**Beklenen:** Her kazıdığında TON diamond çıkmalı!

---

### Test 4: Ölü Gezegen

1. `config.yml` aç
2. `forest` için ore sistemini kapat:
   ```yaml
   forest:
     enabled: false      # KAPALI!
     densityMultiplier: 0.0
     ores: {}
   ```
3. Reload ve yeni forest gezegeni oluştur
4. Kaz

**Beklenen:** Hiçbir ore çıkmamalı!

---

## 📊 Chance (Şans) Sistemi Nasıl Çalışır?

```yaml
chance: 0.35        # 0.0 - 1.0 arası
```

- `0.01` = %1 şans → 1000 blokta 10 ore
- `0.05` = %5 şans → 1000 blokta 50 ore
- `0.10` = %10 şans → 1000 blokta 100 ore
- `0.25` = %25 şans → 1000 blokta 250 ore
- `0.35` = %35 şans → 1000 blokta 350 ore (varsayılan coal)
- `0.50` = %50 şans → Yarı yarıya!
- `0.99` = %99 şans → Neredeyse her blok ore!

### Density Multiplier (Çarpan)

```yaml
densityMultiplier: 2.0    # TÜM şansları 2x artırır
```

**Örnek Hesaplama:**
```yaml
coal:
  chance: 0.30
densityMultiplier: 2.0
```
Gerçek chance: `0.30 × 2.0 = 0.60` (%60!)

**Örnek 2:**
```yaml
diamond:
  chance: 0.04    # %4
densityMultiplier: 5.0
```
Gerçek chance: `0.04 × 5.0 = 0.20` (%20!)

---

## 🛠️ Dosya Değişiklikleri

### 1. `config.yml`
- ✅ `planetTypes` bölümü eklendi (16+ biyom)
- ✅ Her biyom için özel ore profilleri
- ✅ `enabled: false` ile ore kapatma desteği

### 2. `GenerationManager.java`
- ✅ `tryGenerateOre()` metodu tamamen yeniden yazıldı
- ✅ Biyom adından config okuma düzeltildi
- ✅ `generateSolidPlanet()` içine ore entegrasyonu
- ✅ `generateHollowPlanet()` içine kabuk ore desteği

### 3. `ConfigManager.java`
- ✅ `getOreConfigForPlanetType()` metodu düzeltildi
- ✅ Biome name'den doğru path oluşturuyor
- ✅ `features.ores.planetTypes.{biome_name}` yapısı

---

## 🎉 Sonuç

**ÖNCEKİ DURUM:**
❌ Config değişiklikleri etki etmiyordu
❌ Gezegenler hep sphere şeklindeydi
❌ Solid gezegenlerde ore oluşmuyordu
❌ Her biyomda aynı ore dağılımı

**ŞİMDİKİ DURUM:**
✅ Config tamamen fonksiyonel
✅ 28 farklı gezegen şekli
✅ Solid ve hollow gezegenlerde ore oluşuyor
✅ Her biyom kendi ore profiline sahip
✅ Ölü gezegenler yapılabiliyor (hiç ore yok)
✅ Config'den her şey düzenlenebilir

**Test Komutu:**
```
/gezegen reload
```

**Artık yapabileceklerin:**
- Nether gezegenleri (sadece nether ores)
- Kristal gezegenleri (sadece gems)
- Ölü gezegenler (hiç ore yok)
- Ultra zengin gezegenler (densityMultiplier: 10.0)
- Sadece diamond gezegeni (diğer oreler disabled)
- Ve daha fazlası... Her şey senin elinde!

🚀 İyi gezegenler!
