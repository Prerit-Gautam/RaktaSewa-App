package com.example.raktasewa.ui.screens

object Loc {
    fun t(key: String, lang: String): String {
        val map = if (lang == "ne") ne else en
        return map[key] ?: en[key] ?: key
    }

    private val en = mapOf(
        // Language Selection
        "welcome" to "Welcome",
        "select_lang_sub" to "Please select your preferred language to continue.",
        "settings_hint" to "You can change this anytime in settings.",
        "setting_up" to "Setting things up...",

        // Select Blood Group
        "select_blood_group" to "Select Your Blood Group",
        "select_blood_group_sub" to "Choose your blood type to see compatible donors and nearby medical centers.",
        "find_nearby" to "Find Nearby",
        "secure_selection" to "Securely encrypted selection",
        "positive" to "Positive",
        "negative" to "Negative",
        "info_tip" to "Choosing your correct blood type ensures that search results show hospitals with the specific inventory you need.",

        // Finding Blood Banks
        "finding_nearest" to "Finding nearest blood banks...",
        "searching_within" to "Searching within 20km...",
        "checking_availability" to "Checking availability...",
        "verifying_stock" to "Verifying {group} stock levels...",
        "optimizing_routes" to "Optimizing routes...",
        "calculating_arrival" to "Calculating arrival time for retrieval...",
        "syncing_donor" to "Syncing donor data...",
        "connecting_health" to "Connecting to regional health network...",
        "secure_conn" to "Secure Connection Established",

        // Nearby Blood Banks
        "search_placeholder" to "Search blood banks near you...",
        "all_banks" to "All Banks",
        "nearest_first" to "Nearest First",
        "open_now" to "Open Now",
        "emergency_priority" to "Emergency Priority",
        "critical_need" to "Critical Need: O-",
        "critical_need_sub" to "3 banks requesting O negative.",
        "high_stock" to "High Stock",
        "low_stock" to "Low Stock",
        "critical" to "Critical",
        "km_away" to "km away",
        "unit" to "Unit",
        "units" to "Units",
        "call" to "Call",
        "directions" to "Directions",
        "error_title" to "Something went wrong",
        "retry" to "Try Again",
        "no_banks_found" to "No blood banks found",
        "no_banks_sub" to "We couldn't find any blood banks for this type. Try a different blood group.",
        "results_found" to "{count} banks found for {group}",
        "results_sub" to "Showing real-time availability from nearby blood banks."
    )

    private val ne = mapOf(
        // Language Selection
        "welcome" to "स्वागत छ",
        "select_lang_sub" to "अगाडि बढ्नको लागि कृपया आफ्नो मनपर्ने भाषा चयन गर्नुहोस्।",
        "settings_hint" to "तपाईंले यसलाई सेटिङहरूमा जुनसुकै बेला परिवर्तन गर्न सक्नुहुन्छ।",
        "setting_up" to "सेटअप हुँदैछ...",

        // Select Blood Group
        "select_blood_group" to "आफ्नो रगत समूह चयन गर्नुहोस्",
        "select_blood_group_sub" to "अनुकूल दाताहरू र नजिकैका चिकित्सा केन्द्रहरू हेर्न आफ्नो रगत समूह छनौट गर्नुहोस्।",
        "find_nearby" to "नजिकै खोज्नुहोस्",
        "secure_selection" to "सुरक्षित रूपमा इन्क्रिप्टेड चयन",
        "positive" to "पोजिटिभ",
        "negative" to "नेगेटिभ",
        "info_tip" to "तपाईंको सही रगत प्रकार छनोट गर्दा खोज परिणामहरूले तपाईंलाई आवश्यक पर्ने विशिष्ट सूची भएका अस्पतालहरू देखाउने सुनिश्चित गर्दछ।",

        // Finding Blood Banks
        "finding_nearest" to "नजिकैको ब्लड बैंकहरू खोज्दै...",
        "searching_within" to "२० किलोमिटर भित्र खोजी गर्दै...",
        "checking_availability" to "उपलब्धता जाँच गर्दै...",
        "verifying_stock" to "{group} स्टक स्तर प्रमाणीकरण गर्दै...",
        "optimizing_routes" to "मार्गहरू अनुकूलन गर्दै...",
        "calculating_arrival" to "पुग्न लाग्ने समय गणना गर्दै...",
        "syncing_donor" to "दाता डाटा सिंक गर्दै...",
        "connecting_health" to "क्षेत्रीय स्वास्थ्य नेटवर्कमा जडान गर्दै...",
        "secure_conn" to "सुरक्षित जडान स्थापित भयो",

        // Nearby Blood Banks
        "search_placeholder" to "आफ्नो नजिकका ब्लड बैंकहरू खोज्नुहोस्...",
        "all_banks" to "सबै बैंकहरू",
        "nearest_first" to "नजिकैको पहिले",
        "open_now" to "अहिले खुल्ला",
        "emergency_priority" to "आपतकालीन प्राथमिकता",
        "critical_need" to "महत्वपूर्ण आवश्यकता: O-",
        "critical_need_sub" to "३ बैंकहरूले O नेगेटिभ अनुरोध गरिरहेका छन्।",
        "high_stock" to "उच्च स्टक",
        "low_stock" to "कम स्टक",
        "critical" to "नाजुक",
        "km_away" to "किमी टाढा",
        "unit" to "युनिट",
        "units" to "युनिटहरू",
        "call" to "कल गर्नुहोस्",
        "directions" to "दिशा निर्देश",
        "error_title" to "केही गलत भयो",
        "retry" to "पुन: प्रयास गर्नुहोस्",
        "no_banks_found" to "कुनै ब्लड बैंक भेटिएन",
        "no_banks_sub" to "यस प्रकारको लागि ब्लड बैंक भेटिएन। अर्को रक्त समूह प्रयास गर्नुहोस्।",
        "results_found" to "{group} को लागि {count} बैंकहरू भेटिए",
        "results_sub" to "नजिकैका ब्लड बैंकहरूबाट वास्तविक समयको उपलब्धता देखाउँदै।"
    )
}
