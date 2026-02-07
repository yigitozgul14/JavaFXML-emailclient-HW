# AdvanceJavaProject3

JavaFX tabanli basit bir e-posta istemcisi. Uygulama SMTP ile e-posta gonderebilir, IMAP/POP3 ile klasor bazli e-postalari okuyabilir ve secilen mesaja yanit yazabilir.

## Ozellikler
- Coklu host konfigurasyonu ekleme ve secme
- `INBOX` ve `Sent Items` klasorlerinden e-posta listeleme
- Son e-postalari tablo uzerinden goruntuleme
- Yeni e-posta olusturma
- Secilen e-postaya yanit verme
- E-posta gonderirken dosya eki ekleyebilme

## Teknolojiler
- Java 8
- JavaFX (FXML)
- JavaMail (`javax.mail`)
- Apache Ant / NetBeans proje yapisi

## Proje Yapisi
- `src/advancejavaproject3/AdvanceJavaProject3.java`: Uygulama giris noktasi
- `src/advancejavaproject3/FXMLDocumentController.java`: Ana ekran controller'i
- `src/advancejavaproject3/EmailService.java`: SMTP gonderim ve posta kutusu okuma islemleri
- `src/advancejavaproject3/*.fxml`: Arayuz dosyalari

## Calistirma (NetBeans)
1. Projeyi NetBeans ile acin.
2. `javax.mail.jar` bagimliliginin tanimli oldugunu dogrulayin.
3. Projeyi Build & Run yapin.

## Calistirma (Ant)
1. JDK 8 ve Ant kurulu olmali.
2. `javax.mail.jar` sinif yolunda olmali.
3. Proje kokunde su komutu calistirin:

```bash
ant run
```

## Notlar
- Host konfigurasyonu olusturmadan e-posta islemleri baslatilamaz.
- `Sent Items` klasoru adlari sunucuya gore degisebilir; uygulama yaygin adlari otomatik dener.
- Projede `dist/lib/javax.mail.jar` bulunuyor; ortam ayariniza gore classpath guncellemesi gerekebilir.

## Lisans
Bu proje egitim amacli gelistirilmistir.
