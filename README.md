# Java ile 1989 numaralı portta çalışan web sunucusu

Bu eğitim projesi, yalnızca standart Java sınıflarını kullanarak bir HTTP sunucusu çalıştırır. Tarayıcıya öğrencinin adı, numarası ve kısa biyografisini biçimlendirilmiş HTML olarak gönderir. Harici kütüphane kullanılmamıştır.

## Gereksinim
Java 8 veya daha yeni bir JDK.

## Derleme ve çalıştırma
Terminali `SimpleWebServer.java` dosyasının bulunduğu klasörde açın ve şu komutları çalıştırın:

```sh
javac -encoding UTF-8 SimpleWebServer.java
java SimpleWebServer
```

Tarayıcıda `http://localhost:1989` adresini açın. Sunucuyu durdurmak için `Ctrl+C` tuşlarına basın. Ad, öğrenci numarası ve biyografi kaynak dosyanın başındaki `PROFILE` sabitinde düzenlenebilir.
