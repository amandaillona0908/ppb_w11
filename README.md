# 🍡 Waroeng Legit

> Aplikasi marketplace jajan pasar berbasis **Jetpack Compose** dengan sistem **role-based access** untuk Admin dan Pembeli, dilengkapi fitur CRUD produk, keranjang belanja, dan manajemen pesanan real-time.

## 👨‍🎓 Identitas

| Field | Detail |
|-------|--------|
| **Nama** | Amanda Illona Farrel |
| **NRP** | 5025221056 |
| **Mata Kuliah** | Pemrograman Perangkat Bergerak (C)|

## ✨ Fitur Aplikasi

### 🔐 Role-Based Access
- 🛠️ **Admin** → Dapat mengelola produk (CRUD), memantau pesanan masuk, dan mengubah status pesanan.
- 🛒 **Pembeli** → Dapat menelusuri produk, menambahkan ke keranjang, dan melakukan pemesanan.
- 🏠 **Login Screen** → Pemilihan peran (Admin / Pembeli) di halaman awal tanpa password.

### 📦 Manajemen Produk *(Admin)*
- ➕ **Tambah Produk** → Form input nama, harga, deskripsi, dan kategori jajan.
- ✏️ **Edit Produk** → Ubah data produk yang sudah ada secara langsung.
- 🗑️ **Hapus Produk** → Dilengkapi dialog konfirmasi sebelum menghapus.
- 🗂️ **Kategori Jajan** → Gorengan, Kue Basah, Kue Kering, Minuman, dan Lainnya.

### 🧾 Manajemen Pesanan *(Admin)*
- 📊 **Dashboard** → Ringkasan total produk, total pesanan, pesanan menunggu, dan omzet.
- 🔔 **Notifikasi Badge** → Indikator jumlah pesanan yang belum diproses.
- 🔄 **Update Status** → Alur status pesanan: *Menunggu → Diproses → Selesai / Dibatalkan*.

### 🛍️ Belanja *(Pembeli)*
- 🔍 **Pencarian** → Cari produk berdasarkan nama secara real-time.
- 🏷️ **Filter Kategori** → Filter produk berdasarkan jenis jajan.
- 🛒 **Keranjang Belanja** → Tambah, kurangi, atau hapus item dengan update jumlah otomatis.
- ✅ **Checkout** → Pesanan langsung masuk ke panel Admin setelah dikonfirmasi.

### 🎨 UI / UX
- 📭 **Empty State** → Tampilan informatif saat produk atau keranjang kosong.
- ✅ **Validasi Input** → Kolom harga hanya menerima angka, mencegah input tidak valid.
- ⏳ **Loading Indicator** → `CircularProgressIndicator` saat menyimpan produk.
- 👤 **Profil Editable** → Nama dan kelas dapat diubah langsung dari dalam aplikasi.
- 🌏 **Bahasa Jawa** → Sapaan dan teks antarmuka menggunakan campuran Bahasa Jawa (*Monggo, Mas/Mbak!*).
- 🎨 **Tema Hangat** → Warna khas coklat-oranye (`#D4730A`) yang mencerminkan nuansa jajan pasar tradisional.

## 🛠️ Tech Stack

![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)
![Material 3](https://img.shields.io/badge/Material%20Design%203-757575?style=for-the-badge&logo=materialdesign&logoColor=white)

## 💡 Yang Dipelajari

- Implementasi **Role-Based Navigation** menggunakan `when` expression dan state `currentRole` untuk memisahkan alur Admin dan Pembeli.
- Penerapan **Shared State Management** dengan `mutableStateListOf` yang dishare antar screen sehingga data produk dan pesanan sinkron secara real-time.
- Penggunaan **Enum Class** (`KategoriJajan`, `StatusPesanan`) untuk merepresentasikan data kategorikal dengan label dan properti tambahan.
- Pembuatan **Data Class bertingkat** (`Order` yang mengandung `List<CartItem>`, dan `CartItem` yang mengandung `Product`).
- Penggunaan **`LaunchedEffect`** untuk inisialisasi data awal hanya satu kali saat composable pertama kali ditampilkan.
- Penerapan **`AlertDialog`** untuk konfirmasi aksi destruktif (hapus produk) demi mencegah kesalahan pengguna.
- Implementasi **`BadgedBox`** dari Material 3 untuk menampilkan jumlah item keranjang secara dinamis pada ikon navigasi.
- Penggunaan **`Scaffold`** lengkap dengan `topBar`, `bottomBar`, `floatingActionButton`, dan `snackbarHost` secara bersamaan.
- Pengaturan **`FilterChip`** dengan `horizontalScroll` untuk filter kategori yang responsif tanpa nested lazy layout.
- Penerapan **`String.format(Locale.getDefault(), "%,d")`** untuk format harga ribuan yang sesuai locale perangkat.

---

<p align="center">
  🍡 Dibuat untuk memenuhi tugas <strong>Pemrograman Perangkat Bergerak</strong> 🍡
</p>
