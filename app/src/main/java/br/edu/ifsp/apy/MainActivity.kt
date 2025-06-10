package br.edu.ifsp.apy

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.PackageManagerCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.edu.ifsp.apy.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {



    private val activityMainActivity: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    companion object {
        private val GALERY_PERMISSION = Manifest.permission.READ_EXTERNAL_STORAGE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activityMainActivity.root)
    }

    private fun galeryPermissionCheck() {
        val acceptGaleryPermission = checkPermission(GALERY_PERMISSION)
    }

    private fun checkPermission(permission: String) {
        ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
    }
}