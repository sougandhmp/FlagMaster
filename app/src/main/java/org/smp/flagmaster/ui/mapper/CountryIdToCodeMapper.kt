package org.smp.flagmaster.ui.mapper

import javax.inject.Inject

/**
 * Maps country IDs to their corresponding country codes.
 */
class CountryIdToCodeMapper @Inject constructor() {

    /**
     * Maps a country ID to its corresponding country code.
     *
     * @param countryId The ID of the country.
     * @return The corresponding country code, or an empty string if not found.
     */
    operator fun invoke(countryId: Int): String = countryIdToCodeMap[countryId] ?: ""

    private val countryIdToCodeMap = mapOf(
        13 to "AW",     // Aruba
        23 to "BZ",     // Belize
        60 to "CZ",     // Czech Republic
        66 to "EC",     // Ecuador
        81 to "GA",     // Gabon
        101 to "IN",    // India
        113 to "JP",    // Japan
        114 to "JE",    // Jersey
        122 to "KG",    // Kyrgyzstan
        126 to "LS",    // Lesotho
        141 to "MQ",    // Martinique
        160 to "NZ",    // New Zealand
        174 to "PY",    // Paraguay
        192 to "PM",    // Saint Pierre and Miquelon
        230 to "TM",    // Turkmenistan
        235 to "AE",    // United Arab Emirates
        // 🔁 Add the rest of your IDs here as needed
    )
}