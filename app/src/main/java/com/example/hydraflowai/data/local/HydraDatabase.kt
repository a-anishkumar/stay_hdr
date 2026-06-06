package com.example.hydraflowai.data.local

import android.content.Context
import com.example.hydraflowai.data.local.dao.HydraDao
import com.example.hydraflowai.data.local.dao.SqliteHydraDao
import kotlinx.coroutines.CoroutineScope

class HydraDatabase private constructor(context: Context, scope: CoroutineScope) {
    
    private val helper = HydraSQLiteHelper(context)
    private val dao: HydraDao = SqliteHydraDao(helper, scope)

    fun hydraDao(): HydraDao = dao

    companion object {
        @Volatile
        private var INSTANCE: HydraDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): HydraDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = HydraDatabase(context, scope)
                INSTANCE = instance
                instance
            }
        }
    }
}
