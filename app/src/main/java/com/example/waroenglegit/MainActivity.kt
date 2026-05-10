package com.example.waroenglegit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

// ==================== DATA MODEL ====================

enum class KategoriJajan(val label: String, val emoji: String) {
    GORENGAN("Gorengan", "🍢"),
    KUE_BASAH("Kue Basah", "🍰"),
    KUE_KERING("Kue Kering", "🍪"),
    MINUMAN("Minuman", "🥤"),
    LAINNYA("Lainnya", "🛍️")
}

enum class StatusPesanan(val label: String, val color: Color) {
    MENUNGGU("Menunggu", Color(0xFFF5A623)),
    DIPROSES("Diproses", Color(0xFF2196F3)),
    SELESAI("Selesai", Color(0xFF4CAF50)),
    DIBATALKAN("Dibatalkan", Color(0xFFF44336))
}

data class Product(
    val id: Long = System.currentTimeMillis(),
    val name: String,
    val price: String,
    val description: String,
    val kategori: KategoriJajan = KategoriJajan.LAINNYA
)

data class CartItem(
    val product: Product,
    val quantity: Int = 1
)

data class Order(
    val id: Long = System.currentTimeMillis(),
    val buyerName: String,
    val items: List<CartItem>,
    val total: Long,
    var status: StatusPesanan = StatusPesanan.MENUNGGU
)

// ==================== MAIN ACTIVITY ====================

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WaroengLegitTheme {
                AppRoot()
            }
        }
    }
}

@Composable
fun AppRoot() {
    // Shared state antara admin & buyer
    val productList = remember { mutableStateListOf<Product>() }
    val orderList = remember { mutableStateListOf<Order>() }
    var currentRole by remember { mutableStateOf<String?>(null) } // null = belum pilih role

    LaunchedEffect(Unit) {
        if (productList.isEmpty()) {
            productList.add(Product(name = "Brownies Lumer", price = "15000", description = "Cokelat melimpah, lumer di mulut.", kategori = KategoriJajan.KUE_BASAH))
            productList.add(Product(name = "Onde-onde", price = "3000", description = "Isi kacang ijo, wijen renyah.", kategori = KategoriJajan.KUE_BASAH))
            productList.add(Product(name = "Tempe Mendoan", price = "2000", description = "Tempe tipis, digoreng setengah matang.", kategori = KategoriJajan.GORENGAN))
            productList.add(Product(name = "Es Dawet", price = "5000", description = "Seger, manis, legit!", kategori = KategoriJajan.MINUMAN))
            productList.add(Product(name = "Klepon", price = "2000", description = "Isi gula jawa, tabur kelapa.", kategori = KategoriJajan.KUE_BASAH))
        }
    }

    when (currentRole) {
        null -> LoginScreen(onRoleSelected = { currentRole = it })
        "admin" -> AdminScreen(
            productList = productList,
            orderList = orderList,
            onLogout = { currentRole = null }
        )
        "buyer" -> BuyerScreen(
            productList = productList,
            onPlaceOrder = { order -> orderList.add(order) },
            onLogout = { currentRole = null }
        )
    }
}

// ==================== LOGIN / PILIH ROLE ====================

@Composable
fun LoginScreen(onRoleSelected: (String) -> Unit) {
    var showAdminDialog by remember { mutableStateOf(false) }
    var adminKeyInput by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }
    val adminKey = "waroenglegit"

    if (showAdminDialog) {
        AlertDialog(
            onDismissRequest = { 
                showAdminDialog = false 
                adminKeyInput = ""
                isError = false
                passwordVisible = false
            },
            title = { Text("Verifikasi Admin") },
            text = {
                Column {
                    Text("Masukkan kode akses untuk masuk sebagai Admin.")
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = adminKeyInput,
                        onValueChange = { 
                            adminKeyInput = it
                            isError = false
                        },
                        label = { Text("Kode Akses") },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (passwordVisible) "Sembunyikan password" else "Tampilkan password"
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        isError = isError,
                        supportingText = { if (isError) Text("Kode salah!", color = Color.Red) },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (adminKeyInput == adminKey) {
                        showAdminDialog = false
                        onRoleSelected("admin")
                    } else {
                        isError = true
                    }
                }) {
                    Text("Masuk")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAdminDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.waroeng_legit_logo_photoroom_copy),
            contentDescription = "Logo Waroeng Legit",
            modifier = Modifier.size(200.dp)
        )
        // Gunakan offset negatif untuk menarik teks ke atas (mendekati logo)
        // karena biasanya file gambar punya whitespace di bagian bawahnya.
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.offset(y = (-40).dp)
        ) {
            Text("Waroeng Legit", fontSize = 30.sp, fontWeight = FontWeight.ExtraBold)
            Text("Jajan pasar siswa terbaik!", color = Color.Gray, fontSize = 14.sp)
        }
        
        Spacer(Modifier.height(20.dp))
        Text("Kamu masuk sebagai:", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
        Spacer(Modifier.height(20.dp))

        // Tombol Admin
        Card(
            onClick = { showAdminDialog = true },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("🛠️", fontSize = 36.sp)
                Spacer(Modifier.width(16.dp))
                Column {
                    Text("Admin", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text("Kelola produk & pesanan", color = Color.Gray, fontSize = 13.sp)
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Tombol Pembeli
        Card(
            onClick = { onRoleSelected("buyer") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("🛒", fontSize = 36.sp)
                Spacer(Modifier.width(16.dp))
                Column {
                    Text("Pembeli", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text("Lihat & beli jajan", color = Color.Gray, fontSize = 13.sp)
                }
            }
        }
    }
}

// ==================== ADMIN SCREEN ====================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    productList: MutableList<Product>,
    orderList: MutableList<Order>,
    onLogout: () -> Unit
) {
    var currentTab by remember { mutableStateOf("dashboard") }
    var editingProduct by remember { mutableStateOf<Product?>(null) }
    var showAddEdit by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        when {
                            showAddEdit -> if (editingProduct != null) "Edit Produk" else "Tambah Produk"
                            currentTab == "dashboard" -> "Dashboard"
                            currentTab == "produk" -> "Kelola Produk"
                            else -> "Pesanan Masuk"
                        },
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    if (showAddEdit) {
                        IconButton(onClick = { showAddEdit = false; editingProduct = null }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                },
                actions = {
                    if (!showAddEdit) {
                        IconButton(onClick = onLogout) {
                            Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Logout")
                        }
                    }
                }
            )
        },
        bottomBar = {
            if (!showAddEdit) {
                NavigationBar {
                    NavigationBarItem(
                        selected = currentTab == "dashboard",
                        onClick = { currentTab = "dashboard" },
                        label = { Text("Dashboard") },
                        icon = { Icon(Icons.Default.Home, contentDescription = null) }
                    )
                    NavigationBarItem(
                        selected = currentTab == "produk",
                        onClick = { currentTab = "produk" },
                        label = { Text("Produk") },
                        icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = null) }
                    )
                    NavigationBarItem(
                        selected = currentTab == "pesanan",
                        onClick = { currentTab = "pesanan" },
                        label = { Text("Pesanan") },
                        icon = {
                            BadgedBox(badge = {
                                val pending = orderList.count { it.status == StatusPesanan.MENUNGGU }
                                if (pending > 0) Badge { Text("$pending") }
                            }) {
                                Icon(Icons.Default.Notifications, contentDescription = null)
                            }
                        }
                    )
                }
            }
        },
        floatingActionButton = {
            if (currentTab == "produk" && !showAddEdit) {
                ExtendedFloatingActionButton(
                    onClick = { editingProduct = null; showAddEdit = true },
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Tambah Produk")
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            if (showAddEdit) {
                AddEditProductScreen(
                    existingProduct = editingProduct,
                    onProductSaved = { saved ->
                        if (editingProduct != null) {
                            val idx = productList.indexOfFirst { it.id == saved.id }
                            if (idx >= 0) productList[idx] = saved
                        } else {
                            productList.add(0, saved)
                        }
                        val msg = if (editingProduct != null) "Produk diperbarui!" else "Produk ditambahkan!"
                        editingProduct = null
                        showAddEdit = false
                        scope.launch { snackbarHostState.showSnackbar(msg) }
                    }
                )
            } else {
                when (currentTab) {
                    "dashboard" -> AdminDashboardScreen(productList, orderList)
                    "produk" -> AdminProdukScreen(
                        products = productList,
                        onEdit = { product -> editingProduct = product; showAddEdit = true },
                        onDelete = { product ->
                            productList.remove(product)
                            scope.launch { snackbarHostState.showSnackbar("${product.name} dihapus.") }
                        }
                    )
                    "pesanan" -> AdminPesananScreen(
                        orders = orderList,
                        onUpdateStatus = { order, status ->
                            val idx = orderList.indexOf(order)
                            if (idx >= 0) orderList[idx] = order.copy(status = status)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun AdminDashboardScreen(products: List<Product>, orders: List<Order>) {
    val totalProduk = products.size
    val totalPesanan = orders.size
    val pesananMenunggu = orders.count { it.status == StatusPesanan.MENUNGGU }
    val totalOmzet = orders.filter { it.status == StatusPesanan.SELESAI }.sumOf { it.total }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Selamat datang, Admin! 👋", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
            Text("Ini ringkasan Waroeng Legit hari ini.", color = Color.Gray)
            Spacer(Modifier.height(8.dp))
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DashboardCard(modifier = Modifier.weight(1f), emoji = "📦", label = "Total Produk", value = "$totalProduk")
                DashboardCard(modifier = Modifier.weight(1f), emoji = "🧾", label = "Total Pesanan", value = "$totalPesanan")
            }
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DashboardCard(modifier = Modifier.weight(1f), emoji = "⏳", label = "Menunggu", value = "$pesananMenunggu", valueColor = Color(0xFFF5A623))
                DashboardCard(modifier = Modifier.weight(1f), emoji = "💰", label = "Omzet", value = "Rp ${formatHarga(totalOmzet.toString())}", valueColor = Color(0xFF4CAF50))
            }
        }
        if (orders.isNotEmpty()) {
            item {
                Spacer(Modifier.height(4.dp))
                Text("Pesanan Terbaru", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            items(orders.takeLast(3).reversed()) { order ->
                OrderCard(order = order, showActions = false, onUpdateStatus = { _, _ -> })
            }
        }
    }
}

@Composable
fun DashboardCard(
    modifier: Modifier = Modifier,
    emoji: String,
    label: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.primary
) {
    Card(modifier = modifier, shape = RoundedCornerShape(16.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(emoji, fontSize = 28.sp)
            Spacer(Modifier.height(8.dp))
            Text(value, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = valueColor)
            Text(label, color = Color.Gray, fontSize = 12.sp)
        }
    }
}

@Composable
fun AdminProdukScreen(
    products: List<Product>,
    onEdit: (Product) -> Unit,
    onDelete: (Product) -> Unit
) {
    var productToDelete by remember { mutableStateOf<Product?>(null) }

    productToDelete?.let { product ->
        AlertDialog(
            onDismissRequest = { productToDelete = null },
            title = { Text("Hapus Produk?") },
            text = { Text("Yakin mau hapus \"${product.name}\"?\nOra iso dibalekke maneh yo.") },
            confirmButton = {
                TextButton(onClick = { onDelete(product); productToDelete = null }) {
                    Text("Hapus", color = Color.Red, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { productToDelete = null }) { Text("Batal") }
            }
        )
    }

    if (products.isEmpty()) {
        EmptyState(emoji = "📦", message = "Belum ada produk.\nTambah produk lewat tombol di bawah.")
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(products) { product ->
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(product.kategori.emoji, fontSize = 28.sp)
                            Spacer(Modifier.width(10.dp))
                            Column {
                                Text(product.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text(product.kategori.label, color = MaterialTheme.colorScheme.primary, fontSize = 12.sp)
                            }
                        }
                        Text("Rp ${formatHarga(product.price)}", fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(product.description, color = Color.DarkGray, fontSize = 13.sp)
                    Spacer(Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                        OutlinedButton(
                            onClick = { onEdit(product) },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Edit", fontSize = 13.sp)
                        }
                        Spacer(Modifier.width(8.dp))
                        Button(
                            onClick = { productToDelete = product },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336)),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Hapus", fontSize = 13.sp)
                        }
                    }
                }
            }
        }
        item { Spacer(Modifier.height(80.dp)) } // ruang FAB
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPesananScreen(
    orders: List<Order>,
    onUpdateStatus: (Order, StatusPesanan) -> Unit
) {
    if (orders.isEmpty()) {
        EmptyState(emoji = "🧾", message = "Belum ada pesanan masuk.")
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(orders.reversed()) { order ->
            OrderCard(order = order, showActions = true, onUpdateStatus = onUpdateStatus)
        }
    }
}

@Composable
fun OrderCard(order: Order, showActions: Boolean, onUpdateStatus: (Order, StatusPesanan) -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(order.buyerName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("${order.items.sumOf { it.quantity }} item • Rp ${formatHarga(order.total.toString())}", color = Color.Gray, fontSize = 13.sp)
                }
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = order.status.color.copy(alpha = 0.15f)
                ) {
                    Text(
                        order.status.label,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        color = order.status.color,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Detail item pesanan
            Spacer(Modifier.height(8.dp))
            order.items.forEach { item ->
                Text("• ${item.product.name} x${item.quantity}", fontSize = 13.sp, color = Color.DarkGray)
            }

            // Tombol update status (hanya admin)
            if (showActions && order.status != StatusPesanan.SELESAI && order.status != StatusPesanan.DIBATALKAN) {
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (order.status == StatusPesanan.MENUNGGU) {
                        Button(
                            onClick = { onUpdateStatus(order, StatusPesanan.DIPROSES) },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) { Text("Proses", fontSize = 13.sp) }
                    }
                    if (order.status == StatusPesanan.DIPROSES) {
                        Button(
                            onClick = { onUpdateStatus(order, StatusPesanan.SELESAI) },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) { Text("Selesai", fontSize = 13.sp) }
                    }
                    OutlinedButton(
                        onClick = { onUpdateStatus(order, StatusPesanan.DIBATALKAN) },
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) { Text("Batal", fontSize = 13.sp, color = Color.Red) }
                }
            }
        }
    }
}

// ==================== ADD / EDIT PRODUCT SCREEN ====================

@Composable
fun AddEditProductScreen(
    existingProduct: Product?,
    onProductSaved: (Product) -> Unit
) {
    var name by remember { mutableStateOf(existingProduct?.name ?: "") }
    var price by remember { mutableStateOf(existingProduct?.price ?: "") }
    var desc by remember { mutableStateOf(existingProduct?.description ?: "") }
    var kategori by remember { mutableStateOf(existingProduct?.kategori ?: KategoriJajan.LAINNYA) }
    var isLoading by remember { mutableStateOf(false) }
    var priceError by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = name, onValueChange = { name = it },
            label = { Text("Nama Jajan") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )
        OutlinedTextField(
            value = price,
            onValueChange = {
                price = it.filter { c -> c.isDigit() }
                priceError = if (price.isEmpty()) "Harga tidak boleh kosong" else ""
            },
            label = { Text("Harga (Rp)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            isError = priceError.isNotEmpty(),
            supportingText = { if (priceError.isNotEmpty()) Text(priceError, color = Color.Red) }
        )
        OutlinedTextField(
            value = desc, onValueChange = { desc = it },
            label = { Text("Deskripsi") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            shape = RoundedCornerShape(12.dp)
        )
        Text("Kategori Jajan", fontWeight = FontWeight.SemiBold)
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            KategoriJajan.entries.forEach { k ->
                FilterChip(
                    selected = kategori == k,
                    onClick = { kategori = k },
                    label = { Text("${k.emoji} ${k.label}") }
                )
            }
        }
        Button(
            onClick = {
                isLoading = true
                val saved = Product(
                    id = existingProduct?.id ?: System.currentTimeMillis(),
                    name = name, price = price, description = desc, kategori = kategori
                )
                scope.launch { delay(800); onProductSaved(saved) }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = name.isNotBlank() && price.isNotBlank() && !isLoading,
            shape = RoundedCornerShape(12.dp)
        ) {
            if (isLoading) CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
            else Text(if (existingProduct != null) "Simpan Perubahan" else "Tambah Produk", fontWeight = FontWeight.Bold)
        }
    }
}

// ==================== BUYER SCREEN ====================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuyerScreen(
    productList: List<Product>,
    onPlaceOrder: (Order) -> Unit,
    onLogout: () -> Unit
) {
    var currentTab by remember { mutableStateOf("home") }
    val cartItems = remember { mutableStateListOf<CartItem>() }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var buyerName by remember { mutableStateOf("Pembeli") }
    var buyerKelas by remember { mutableStateOf("XII IPA 1") }
    var buyerNomor by remember { mutableStateOf("081216686381") }

    val cartCount = cartItems.sumOf { it.quantity }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        when (currentTab) {
                            "cart" -> "Keranjang"
                            "profile" -> "Profil"
                            else -> "Waroeng Legit"
                        },
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    if (currentTab == "home") {
                        BadgedBox(badge = { if (cartCount > 0) Badge { Text("$cartCount") } }) {
                            IconButton(onClick = { currentTab = "cart" }) {
                                Icon(Icons.Default.ShoppingCart, contentDescription = "Keranjang")
                            }
                        }
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = currentTab == "home",
                    onClick = { currentTab = "home" },
                    label = { Text("Home") },
                    icon = { Icon(Icons.Default.Home, contentDescription = null) }
                )
                NavigationBarItem(
                    selected = currentTab == "cart",
                    onClick = { currentTab = "cart" },
                    label = { Text("Keranjang") },
                    icon = {
                        BadgedBox(badge = { if (cartCount > 0) Badge { Text("$cartCount") } }) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = null)
                        }
                    }
                )
                NavigationBarItem(
                    selected = currentTab == "profile",
                    onClick = { currentTab = "profile" },
                    label = { Text("Profil") },
                    icon = { Icon(Icons.Default.Person, contentDescription = null) }
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (currentTab) {
                "home" -> BuyerHomeScreen(
                    products = productList,
                    onAddToCart = { product ->
                        val idx = cartItems.indexOfFirst { it.product.id == product.id }
                        if (idx >= 0) cartItems[idx] = cartItems[idx].copy(quantity = cartItems[idx].quantity + 1)
                        else cartItems.add(CartItem(product))
                        scope.launch { snackbarHostState.showSnackbar("${product.name} ditambahkan ke keranjang!") }
                    }
                )
                "cart" -> CartScreen(
                    cartItems = cartItems,
                    onUpdateQuantity = { item, delta ->
                        val idx = cartItems.indexOf(item)
                        if (idx >= 0) {
                            val newQty = item.quantity + delta
                            if (newQty <= 0) cartItems.removeAt(idx) else cartItems[idx] = item.copy(quantity = newQty)
                        }
                    },
                    onCheckout = {
                        val total = cartItems.sumOf { it.product.price.toLongOrNull()?.times(it.quantity) ?: 0L }
                        onPlaceOrder(Order(buyerName = buyerName, items = cartItems.toList(), total = total))
                        cartItems.clear()
                        currentTab = "home"
                        scope.launch { snackbarHostState.showSnackbar("Matur nuwun! Pesanan dikirim ke admin 🙏") }
                    }
                )
                "profile" -> BuyerProfileScreen(
                    name = buyerName,
                    kelas = buyerKelas,
                    nomor = buyerNomor,
                    onSave = { n, k, u ->
                        buyerName = n
                        buyerKelas = k
                        buyerNomor = u
                        scope.launch { snackbarHostState.showSnackbar("Profil diperbarui!") }
                    },
                    onLogout = onLogout
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuyerHomeScreen(products: List<Product>, onAddToCart: (Product) -> Unit) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedKategori by remember { mutableStateOf<KategoriJajan?>(null) }

    val filteredProducts = products.filter {
        val matchSearch = searchQuery.isEmpty() || it.name.contains(searchQuery, ignoreCase = true)
        val matchKategori = selectedKategori == null || it.kategori == selectedKategori
        matchSearch && matchKategori
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Monggo, Mas/Mbak! 👋", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
            Text("Jajan apa hari iki?", color = Color.Gray, modifier = Modifier.padding(bottom = 12.dp))
            OutlinedTextField(
                value = searchQuery, onValueChange = { searchQuery = it },
                placeholder = { Text("Goleki jajan...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Clear, contentDescription = "Hapus")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(selected = selectedKategori == null, onClick = { selectedKategori = null }, label = { Text("Semua") })
                KategoriJajan.entries.forEach { k ->
                    FilterChip(
                        selected = selectedKategori == k,
                        onClick = { selectedKategori = if (selectedKategori == k) null else k },
                        label = { Text("${k.emoji} ${k.label}") }
                    )
                }
            }
            Spacer(Modifier.height(4.dp))
        }

        if (filteredProducts.isEmpty()) {
            item { EmptyState(emoji = "🍱", message = if (searchQuery.isNotEmpty() || selectedKategori != null) "Jajan ora ketemu 😔\nCoba cari yang lain." else "Belum ada produk nih.\nTunggu sebentar ya!") }
        }

        items(filteredProducts) { product ->
            // Buyer card — tanpa tombol edit/hapus
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(product.kategori.emoji, fontSize = 28.sp)
                            Spacer(Modifier.width(10.dp))
                            Column {
                                Text(product.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text(product.kategori.label, color = MaterialTheme.colorScheme.primary, fontSize = 12.sp)
                            }
                        }
                        Text("Rp ${formatHarga(product.price)}", fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary, fontSize = 16.sp)
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(product.description, color = Color.DarkGray, fontSize = 14.sp)
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = { onAddToCart(product) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Tambah ke Keranjang")
                    }
                }
            }
        }
    }
}

@Composable
fun CartScreen(
    cartItems: List<CartItem>,
    onUpdateQuantity: (CartItem, Int) -> Unit,
    onCheckout: () -> Unit
) {
    val total = cartItems.sumOf { it.product.price.toLongOrNull()?.times(it.quantity) ?: 0L }

    if (cartItems.isEmpty()) {
        EmptyState(emoji = "🛒", message = "Keranjange kosong, Mas/Mbak!\nYuk pilih jajan dulu.")
        return
    }

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(cartItems) { item ->
                Card(shape = RoundedCornerShape(16.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Text(item.product.kategori.emoji, fontSize = 28.sp)
                            Spacer(Modifier.width(10.dp))
                            Column {
                                Text(item.product.name, fontWeight = FontWeight.Bold)
                                Text("Rp ${formatHarga(item.product.price)}", color = MaterialTheme.colorScheme.primary, fontSize = 13.sp)
                            }
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            OutlinedButton(
                                onClick = { onUpdateQuantity(item, -1) },
                                modifier = Modifier.size(32.dp),
                                contentPadding = PaddingValues(0.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) { Text("-", fontSize = 18.sp, fontWeight = FontWeight.Bold) }
                            Text("${item.quantity}", fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 12.dp))
                            Button(
                                onClick = { onUpdateQuantity(item, 1) },
                                modifier = Modifier.size(32.dp),
                                contentPadding = PaddingValues(0.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) { Text("+", fontSize = 18.sp, fontWeight = FontWeight.Bold) }
                        }
                    }
                }
            }
        }
        Surface(shadowElevation = 8.dp) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Total:", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("Rp ${formatHarga(total.toString())}", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
                }
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = onCheckout,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp)
                ) { Text("Pesen Saiki 🙏", fontWeight = FontWeight.Bold, fontSize = 16.sp) }
            }
        }
    }
}

@Composable
fun BuyerProfileScreen(name: String, kelas: String, nomor: String, onSave: (String, String, String) -> Unit, onLogout: () -> Unit) {
    var editName by remember { mutableStateOf(name) }
    var editKelas by remember { mutableStateOf(kelas) }
    var editNomor by remember { mutableStateOf(nomor) }
    var isEditing by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(24.dp))
        Icon(Icons.Default.AccountCircle, contentDescription = null, modifier = Modifier.size(100.dp), tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(16.dp))
        if (isEditing) {
            OutlinedTextField(value = editName, onValueChange = { editName = it }, label = { Text("Nama") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = editKelas, onValueChange = { editKelas = it }, label = { Text("Kelas") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(value = editNomor, onValueChange = { editNomor = it }, label = { Text("Nomor") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
            Spacer(Modifier.height(16.dp))
            Button(onClick = { onSave(editName, editKelas, editNomor); isEditing = false }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) { Text("Simpen Profil") }
            Spacer(Modifier.height(8.dp))
            OutlinedButton(onClick = { isEditing = false }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) { Text("Batal") }
        } else {
            Text(name, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text(kelas, color = Color.Gray, fontSize = 16.sp)
            Spacer(Modifier.height(16.dp))
            OutlinedButton(onClick = { isEditing = true }, shape = RoundedCornerShape(12.dp)) {
                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("Edit Profil")
            }
            Spacer(Modifier.height(8.dp))
            OutlinedButton(onClick = onLogout, shape = RoundedCornerShape(12.dp)) {
                Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("Ganti Role", color = Color.Red)
            }
        }
    }
}

// ==================== SHARED COMPONENTS ====================

@Composable
fun EmptyState(emoji: String, message: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(emoji, fontSize = 64.sp)
        Spacer(Modifier.height(12.dp))
        Text(message, textAlign = TextAlign.Center, color = Color.Gray, fontSize = 16.sp)
    }
}

// ==================== THEME & UTILITIES ====================

fun formatHarga(price: String): String {
    val num = price.toLongOrNull() ?: return price
    return String.format(Locale.getDefault(), "%,d", num).replace(',', '.')
}

@Composable
fun WaroengLegitTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Color(0xFFD4730A),
            onPrimary = Color.White,
            primaryContainer = Color(0xFFFFDDB3),
            onPrimaryContainer = Color(0xFF2B1600),
            secondary = Color(0xFFF5A623),
            background = Color(0xFFFFF8F0),
            surface = Color(0xFFFFF8F0)
        ),
        content = content
    )
}
