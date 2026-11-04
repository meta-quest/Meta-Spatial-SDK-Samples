package com.meta.pixelandtexel.scanner.feature.mrukraycasting.datasource.local

import androidx.room.Room.databaseBuilder
import com.meta.pixelandtexel.scanner.R
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dbModule = module {
    single {
        databaseBuilder(
            androidContext(),
            MrukDatabase::class.java,
            androidContext().getString(R.string.db_name)
        ).build()
    }

    single { get<MrukDatabase>().mrukDao() }
    single { MrukLocalDatasource(get()) }
}