# SkyOrbs 2.0.0 - Build ve Test Talimatları

## 🔧 Düzeltilen Sorunlar

### 1. ItemStack Amount Hatası (ÇÖZÜLDÜ ✅)
**Sorun**: `amount must be greater than 0` hatası
**Çözüm**: Tüm ItemStack oluşturma yerlerinde `Math.max(1, ...)` kullanıldı

### 2. Config Detaylandırması (TAMAMLANDI ✅)
**Sorun**: Config yeterince detaylı değildi
**Çözüm**: 
- Her özellik config'den düzenlenebilir
- Ore generation tam kontrol
- Tree generation biyom bazlı ayarlar
- Structure types detaylı ayarlar
- Dungeon types tam kontrol
- Treasure tiers ve loot tables
- Atmosphere effects detaylı
- 28 shape için ağırlıklar

### 3. Gezegen Çeşitliliği (GELİŞTİRİLDİ ✅)
**Sorun**: Sadece sphere gezegenler oluşuyordu
**Çözüm**:
- 28 farklı shape eklendi
- Shape ağırlıkları config'de ayarlanabilir
- Varsayılan olarak BLOB, HEMISPHERE, ORGANIC gibi çeşitli şekiller daha yüksek ağırlıkta

## 📦 Build Komutu

```bash
cd c:\Users\Koraizen\Desktop\SkyOrbs
mvn clean package
```

## 🎮 Test Adımları

### Test 1: Gezegen Oluşturma
```
/gezegen create test1
```
**Beklenen**: 
- Hata vermeden tamamlanmalı
- %100'e ulaşmalı
- Farklı şekillerde gezegenler oluşmalı

### Test 2: İçi Dolu Gezegen
```
/gezegen create solid_test
```
**Kontrol Et**:
- Madenler içinde mi?
- Ağaçlar yüzeyde mi?
- Zindanlar içinde mi?
- Yapılar var mı?

### Test 3: İçi Boş Gezegen
Config'de hollow probability'yi 1.0 yap:
```yaml
modifiers:
  hollow:
    probability: 1.0
```
Sonra:
```
/gezegen create hollow_test
```
**Kontrol Et**:
- Kabuk var mı?
- İç tüneller var mı?
- İç yapılar var mı?

### Test 4: Farklı Şekiller
Birkaç gezegen oluştur ve şekillerini kontrol et:
```
/gezegen create shape_test_1
/gezegen create shape_test_2
/gezegen create shape_test_3
```
**Beklenen**: Her biri farklı şekilde olmalı (BLOB, ORGANIC, CRATERED, vb.)

## 🐛 Hata Ayıklama

### Eğer hala ItemStack hatası alıyorsanız:
1. Konsolu kontrol edin - hangi satırda hata veriyor?
2. O dosyayı açın ve `new ItemStack(...)` satırlarını bulun
3. `Math.max(1, ...)` eklenmiş mi kontrol edin

### Eğer gezegenler hala sphere ise:
1. Config'i kontrol edin - shape weights doğru mu?
2. Config'i reload edin: `/gezegen reload`
3. Yeni gezegen oluşturun

### Eğer %87'de takılıyorsa:
1. Konsol loglarını kontrol edin
2. Hangi aşamada takıldığını görün
3. O aşamanın kodunu kontrol edin

## 📊 Config Örnekleri

### Daha Fazla Çeşitlilik İçin:
```yaml
generation:
  shapes:
    weights:
      SPHERE: 3.0        # Azaltıldı
      BLOB: 15.0         # Artırıldı
      ORGANIC: 12.0      # Artırıldı
      CRATERED: 10.0     # Artırıldı
      LAYERED: 10.0      # Artırıldı
      WAVE: 10.0         # Artırıldı
```

### Daha Fazla Maden İçin:
```yaml
features:
  ores:
    densityMultiplier: 2.0  # 2x daha fazla maden
```

### Daha Fazla Ağaç İçin:
```yaml
features:
  trees:
    densityMultiplier: 3.0  # 3x daha fazla ağaç
```

## ✅ Başarı Kriterleri

- [ ] Gezegen %100 oluşuyor
- [ ] Hata mesajı yok
- [ ] Madenler içinde spawn oluyor
- [ ] Ağaçlar yüzeyde ve havada değil
- [ ] Zindanlar içinde ve loot var
- [ ] Farklı şekillerde gezegenler oluşuyor
- [ ] İçi boş gezegenlerde tüneller var
- [ ] Yapılar düzgün yerleşiyor

## 🎯 Sonraki Adımlar

Eğer her şey çalışıyorsa:
1. Config'i istediğiniz gibi ayarlayın
2. Farklı biyomları test edin
3. Farklı modifierleri test edin
4. Oyuncularla test edin

## 📝 Notlar

- Her değişiklikten sonra `/gezegen reload` yapın
- Test gezegenlerini `/gezegen delete <name>` ile silebilirsiniz
- Debug modunu açmak için config'de `debug.enabled: true` yapın
