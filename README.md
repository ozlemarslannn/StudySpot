# 📚 StudySpot - Kütüphane Oda Rezervasyon Uygulaması

Günümüzde mobil teknolojiler, bireylerin bilgiye erişim ve iletişim kurma biçimlerinde köklü bir dönüşüm yaratmıştır. Artık kullanıcılar birçok işlemi masaüstü bilgisayarlar yerine taşınabilir cihazlar üzerinden daha hızlı ve pratik bir şekilde gerçekleştirmektedir. **StudySpot**, bu dönüşümden yola çıkarak üniversite kütüphanelerindeki çalışma odalarının mobil cihazlar üzerinden dijital olarak yönetilmesini sağlayan bir Android uygulamasıdır.

---

## 🚀 Özellikler

- 🔐 Firebase Authentication ile kullanıcı kaydı ve girişi
- 👥 Rol bazlı kullanıcı yönetimi (Öğrenci, Akademisyen, Kütüphane Yöneticisi)
- 🏢 Farklı türde odaların rezervasyonu:
  - Akademik Çalışma Odası (Sadece akademik personel)
  - Bireysel Çalışma Odası (Sadece öğrenciler)
  - Grup Çalışma Odası (2-4 kişilik, hem öğrenciler hem de akademik personeller)
  - Toplantı Odası (4-10 kişilik, hem öğrenciler hem de akademik personeller)
- 📄 Firebase Realtime Database entegrasyonu
- 📆 Günlük rezervasyon limiti
- 🧭 Rol bazlı ana ekran ve oda listeleme

---

## 🔒 Kullanıcı ve Rezervasyon Kuralları

- **Yönetici (Admin)** kullanıcılar, tüm rezervasyonları görüntüleyebilir ve silebilir.  
- Her kullanıcı, **aynı gün içerisinde, aynı oda tipinden yalnızca bir rezervasyon yapabilir**.  

## 🛠️ Kullanılan Teknolojiler

- Java
- Android Studio
- Firebase Authentication
- Firebase Realtime Database

---

## 📦 Kurulum ve Çalıştırma

### 1. Projeyi Klonlayın

Proje dosyalarını bilgisayarınıza indirerek veya GitHub arayüzü üzerinden ZIP olarak indirip çıkartarak kullanabilirsiniz.

---

### 2. 🧩 Android Studio’da Açın

- Android Studio’yu başlatın.  
- **File > Open** adımlarını takip edin.  
- İndirdiğiniz proje klasörünü seçin ve açın.


## 3. 🔥 Firebase Ayarları

### 3.1 Firebase Projesi Oluşturma

1. [https://console.firebase.google.com/](https://console.firebase.google.com) adresine gidin.  
2. Google hesabınızla giriş yapın.  
3. **"Proje oluştur"** butonuna tıklayın.  
4. Proje adını girin (örnek: `StudySpotApp`) ve devam edin.  
5. Google Analytics tercihini belirleyin ve projeyi oluşturun.

---

### 3.2 Android Uygulaması Ekleyin

1. Firebase panelinde **Android simgesine** tıklayın.  
2. Aşağıdaki bilgileri girin:
   - **Paket Adı**: `com.ornek.studyspot`  
   - **App nickname**: `StudySpot` (isteğe bağlı)  
   - **SHA-1**: (şimdilik boş bırakabilirsiniz)  
3. **"Register app"** diyerek devam edin.

---

### 3.3 google-services.json Dosyasını Ekleyin

1. `google-services.json` dosyasını indirin.  
2. Projenizin `app/` klasörüne yapıştırın:




---

### 3.4 Firebase SDK Kurulumu

#### 🔧 Proje düzeyindeki `build.gradle` dosyasına:

```gradle
buildscript {
    dependencies {
        classpath 'com.google.gms:google-services:4.3.15'
    }
}
plugins {
    id 'com.android.application'
    id 'com.google.gms.google-services'
}

dependencies {
    implementation 'com.google.firebase:firebase-auth:22.1.1'
    implementation 'com.google.firebase:firebase-database:20.3.0'
}

 ```

 ### 3.5 Firebase Authentication Ayarı
Firebase Console’da sol menüden Authentication > Sign-in method bölümüne girin.

Email/Password yöntemini Enable (Etkinleştir) yapın.

3.6 Realtime Database Ayarı
Firebase Console > Realtime Database > Create Database adımlarını takip edin.

Lokasyon olarak europe-west1 seçebilirsiniz.

⚠️ Uygulama yayına alınmadan önce mutlaka güvenlik kuralları güncellenmelidir.

### 4. 📱 Emülatör Ayarı

1. Android Studio’da üst menüden **AVD Manager**’a girin.  
2. Yeni bir sanal cihaz oluşturun veya mevcut bir cihazı seçin.  
3. Aşağıdaki yapılandırmayı kullanmanız önerilir:

   - **Cihaz**: Medium Phone (örnek: Pixel 4 – 1080x2340 çözünürlük)  
   - **API Level**: 30 veya üzeri  

4. Emülatörü başlatın ve projeyi bu cihazda test edin.


### Uygulama Ekran Görüntüleri

![Image](https://github.com/user-attachments/assets/f4b707ef-49f0-4750-9907-3dc317affe07)    ![Image](https://github.com/user-attachments/assets/29f21c2d-b172-495a-a81e-520543aea6d8)




![Image](https://github.com/user-attachments/assets/1cf9c08b-2cc4-4ed5-a5ec-c10ee5bde54d)   ![Image](https://github.com/user-attachments/assets/30f95217-1410-4cd5-a156-30b705fd571e)


