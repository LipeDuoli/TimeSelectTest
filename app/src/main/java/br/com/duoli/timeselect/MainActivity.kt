package br.com.duoli.timeselect

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import br.com.duoli.timeselect.databinding.TimeItemBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.yarolegovich.discretescrollview.InfiniteScrollAdapter
import com.yarolegovich.discretescrollview.transform.ScaleTransformer
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.util.*
import kotlin.math.min

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val timeDialog2 = TimeSelectDialog(this) { hourOfDay, minutes ->
            Toast.makeText(baseContext, "$hourOfDay $minutes", Toast.LENGTH_SHORT).show()
        }.create()

        fab.setOnClickListener {
            timeDialog2.show()
        }
    }
}
