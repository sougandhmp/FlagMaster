package org.smp.data.asset_data_source

import android.content.Context
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import org.smp.data.asset_data_source.dto.QuestionDto
import org.smp.data.asset_data_source.dto.QuestionWrapper
import javax.inject.Inject

class AssetDataSource @Inject constructor(@param:ApplicationContext private val context: Context) {

    fun loadCountriesFromAssets(): List<QuestionDto> {
        val json = context.assets.open("questions.json").bufferedReader().use { it.readText() }
        return Gson().fromJson(json, QuestionWrapper::class.java).questions
    }
}
