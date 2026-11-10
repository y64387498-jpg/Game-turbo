package com.gg.gamespace

import android.app.*
import android.content.*
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.widget.*

class MainActivity : Activity() {
    private val bg = Color.rgb(15,15,19)
    private val card = Color.rgb(29,29,36)
    private val red = Color.rgb(255,53,72)
    private val muted = Color.rgb(165,165,177)
    private lateinit var content: LinearLayout
    private var tab = 0
    private var fps = "Cihaz destekli"
    private var mode = "Dengeli"
    private var game = "Henüz oyun eklenmedi"
    private var gamePackage: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = bg
        window.navigationBarColor = bg
        home()
    }

    private fun frame(title: String, subtitle: String) {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(d(18), d(18), d(18), d(10))
            setBackgroundColor(bg)
        }
        root.addView(label("GG GAME SPACE", 12, red, true))
        root.addView(label(title, 27, Color.WHITE, true))
        root.addView(label(subtitle, 13, muted, false).apply { setPadding(0,0,0,d(15)) })
        val scroll = ScrollView(this)
        content = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        scroll.addView(content)
        root.addView(scroll, LinearLayout.LayoutParams(-1,0,1f))
        val nav = LinearLayout(this).apply { gravity = Gravity.CENTER }
        listOf("Ana Sayfa","Performans","Araçlar","Ayarlar").forEachIndexed { i, s ->
            nav.addView(Button(this).apply {
                text=s; textSize=10f; isAllCaps=false
                setTextColor(if(tab==i) red else muted); setBackgroundColor(Color.TRANSPARENT)
                setOnClickListener { when(i){0->home();1->performance();2->tools();else->settingsPage()} }
            }, LinearLayout.LayoutParams(0,d(48),1f))
        }
        root.addView(nav)
        setContentView(root)
    }

    private fun home() {
        tab=0; frame("Oyun merkezi","Oyunlarını tek yerden yönet")
        val hero = panel(red)
        hero.addView(label("ULTIMATE GAME MODE",11,Color.WHITE,true))
        hero.addView(label("Oyuna hazır mısın?",24,Color.WHITE,true))
        hero.addView(label("Oyununu seç, sana uygun profili aç.",13,Color.WHITE,false))
        hero.addView(button("OYUN EKLE / SEÇ",Color.WHITE,red){ chooseGame() })
        content.addView(hero); gap(14)
        heading("HIZLI DURUM")
        val stats=LinearLayout(this).apply { orientation=LinearLayout.HORIZONTAL }
        stats.addView(stat("FPS PROFİLİ",fps), LinearLayout.LayoutParams(0,-2,1f))
        stats.addView(stat("PERFORMANS",mode), LinearLayout.LayoutParams(0,-2,1f))
        content.addView(stats); gap(12)
        heading("OYUN KÜTÜPHANESİ")
        val p=panel(card)
        p.addView(label("🎮  $game",16,Color.WHITE,true))
        p.addView(label("Seçtiğin oyunu buradan başlat.",12,muted,false))
        p.addView(button("OYUNU BAŞLAT",Color.WHITE,red){ launchGame() })
        content.addView(p); gap(12)
        heading("HIZLI ARAÇLAR")
        val q=panel(card)
        listOf("Ekran görüntüsü kısayolu","Ekran kaydı kısayolu","Rahatsız etme modu","Dokunmatik profili","Ses profili").forEach {
            q.addView(label("•  $it",14,Color.WHITE,false))
        }
        content.addView(q)
    }

    private fun performance() {
        tab=1; frame("Performans merkezi","FPS profili ve oyun deneyimi")
        heading("FPS PROFİLİ")
        val p=panel(card)
        p.addView(label("Hedef FPS tercihi",16,Color.WHITE,true))
        p.addView(label("Bu seçenek profil tercihidir. Gerçek FPS; ekran, donanım, Android ve oyunun desteğine bağlıdır.",12,muted,false))
        listOf("60 FPS","90 FPS","120 FPS","144 FPS","160 FPS","Cihaz destekli").forEach { value ->
            p.addView(button(value + if(fps==value) "  ✓" else "",Color.WHITE,if(fps==value) red else Color.rgb(60,60,68)) {
                fps=value; performance()
            })
        }
        content.addView(p); gap(14)
        heading("PERFORMANS MODU")
        listOf("Eco","Dengeli","Turbo").forEach { value ->
            val box=panel(card)
            box.addView(label(value,17,Color.WHITE,true))
            box.addView(label(when(value) {
                "Eco"->"Güç tüketimini azaltmaya yönelik profil."
                "Turbo"->"Desteklenen cihaz özellikleriyle birlikte kullan."
                else->"Günlük oyun için dengeli profil."
            },12,muted,false))
            box.addView(button(if(mode==value) "SEÇİLİ" else "SEÇ",Color.WHITE,red){mode=value;performance()})
            content.addView(box); gap(7)
        }
        heading("EKRAN VE DOKUNMATİK")
        setting("Yenileme hızı","Cihaz ekran ayarlarına bağlı")
        setting("Dokunma hassasiyeti","Desteklenen cihazlarda")
        setting("Ekran yönü kilidi","Kullanıcı tercihi")
    }

    private fun tools() {
        tab=2; frame("Oyun araçları","Oyun sırasında kullanabileceğin kısayollar")
        val items=listOf(
            "Ekran görüntüsü" to "Android'in ekran görüntüsü özelliği",
            "Ekran kaydı" to "Cihazın yerleşik ekran kaydı",
            "Rahatsız etme" to "Bildirim ve odak ayarları",
            "Yüzen panel izni" to "Diğer uygulamaların üzerinde gösterme",
            "Oyun başlatıcı" to "Yüklü oyun ve uygulamaları seç"
        )
        items.forEachIndexed { i, pair ->
            val p=panel(card)
            p.addView(label(pair.first,16,Color.WHITE,true))
            p.addView(label(pair.second,12,muted,false))
            p.addView(button("AÇ",Color.WHITE,red) {
                when(i) {
                    2 -> startActivity(Intent(Settings.ACTION_ZEN_MODE_SETTINGS))
                    3 -> startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,Uri.parse("package:$packageName")))
                    4 -> chooseGame()
                    else -> Toast.makeText(this,"Bu kısayol cihazın kendi özelliğiyle kullanılabilir.",Toast.LENGTH_SHORT).show()
                }
            })
            content.addView(p); gap(9)
        }
    }

    private fun settingsPage() {
        tab=3; frame("Uygulama ayarları","GG Game Space kişiselleştirme")
        setting("Tema","Siyah / kırmızı")
        setting("Animasyonlar","Arayüz geçişleri")
        setting("Oyun açılışında panel","İzin ve cihaz desteği gerekir")
        setting("Pil profili","Cihaz özelliklerine bağlı")
        setting("Sürüm","1.0.0")
        val p=panel(card)
        p.addView(label("Sistem izinleri",17,Color.WHITE,true))
        p.addView(label("Uygulama donanımı overclock etmez ve oyunların FPS sınırlarını aşamaz. FPS seçenekleri profil tercihidir.",12,muted,false))
        p.addView(button("ÜSTTE GÖSTERME İZNİ",Color.WHITE,red) {
            startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,Uri.parse("package:$packageName")))
        })
        content.addView(p)
    }

    private fun chooseGame() {
        val intent=Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        val apps=packageManager.queryIntentActivities(intent,0).sortedBy { it.loadLabel(packageManager).toString().lowercase() }
        val names=apps.map { it.loadLabel(packageManager).toString() }.toTypedArray()
        if(names.isEmpty()) return
        AlertDialog.Builder(this).setTitle("Oyun veya uygulama seç").setItems(names) { _, which ->
            game=names[which]; gamePackage=apps[which].activityInfo.packageName; home()
        }.setNegativeButton("İptal",null).show()
    }
    private fun launchGame() {
        val pkg=gamePackage
        if(pkg==null){chooseGame();return}
        packageManager.getLaunchIntentForPackage(pkg)?.let { startActivity(it) }
            ?: Toast.makeText(this,"Uygulama açılamadı.",Toast.LENGTH_SHORT).show()
    }
    private fun setting(title:String, desc:String) {
        val p=panel(card); p.addView(label(title,15,Color.WHITE,true)); p.addView(label(desc,12,muted,false)); content.addView(p); gap(7)
    }
    private fun heading(s:String) { content.addView(label(s,11,red,true).apply { setPadding(0,d(5),0,d(8)) }) }
    private fun stat(k:String,v:String):View {
        val p=panel(card); p.addView(label(k,10,red,true)); p.addView(label(v,16,Color.WHITE,true)); return p
    }
    private fun panel(color:Int)=LinearLayout(this).apply {
        orientation=LinearLayout.VERTICAL; setPadding(d(14),d(13),d(14),d(13))
        background=android.graphics.drawable.GradientDrawable().apply { setColor(color);cornerRadius=d(17).toFloat() }
    }
    private fun label(s:String,size:Int,color:Int,bold:Boolean)=TextView(this).apply {
        text=s;textSize=size.toFloat();setTextColor(color);setPadding(0,d(3),0,d(3))
        if(bold) typeface=Typeface.DEFAULT_BOLD
    }
    private fun button(s:String,fg:Int,bgColor:Int,action:()->Unit)=Button(this).apply {
        text=s;textSize=11f;isAllCaps=false;setTextColor(fg);setBackgroundColor(bgColor)
        setOnClickListener { action() }
    }
    private fun gap(n:Int){content.addView(View(this),LinearLayout.LayoutParams(1,d(n)))}
    private fun d(v:Int)=(v*resources.displayMetrics.density).toInt()
}
