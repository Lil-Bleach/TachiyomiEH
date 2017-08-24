package eu.kanade.tachiyomi.ui.setting

import android.content.Intent
import android.os.Bundle
import android.support.v7.preference.PreferenceScreen
import android.view.View
import eu.kanade.tachiyomi.data.preference.PreferencesHelper
import exh.ui.migration.MetadataFetchDialog
import exh.ui.login.LoginActivity
import uy.kohesive.injekt.injectLazy

/**
 * EH Settings fragment
 */

class SettingsEhFragment : SettingsController() {
    override fun setupPreferenceScreen(screen: PreferenceScreen) = with(screen) {
        title = "E-Hentai"

        switchPreference {
            title = "Enable ExHentai"
            summaryOff = "Requires login"
            key = "enable_exhentai"
            isPersistent = false
            defaultValue = false
            preferences.enableExhentai()
                    .asObservable().subscribeUntilDestroy {
                isChecked = it
            }
        }

        switchPreference {
            title = "Use Hentai@Home Network"
            summary = "Do you wish to load images through the Hentai@Home Network? Disabling this option will reduce the amount of pages you are able to view"
            key = "enable_hah"
            defaultValue = "true"
        }

        switchPreference {
            title = "Show Japanese titles in search results"
            summaryOn = "Currently showing Japanese titles in search results. Clear the chapter cache after changing this (in the Advanced section)"
            summaryOff = "Currently showing English/Romanized titles in search results. Clear the chapter cache after changing this (in the Advanced section)"
            key = "use_jp_title"
            defaultValue = "false"
        }

        switchPreference {
            defaultValue = "true"
            key = "secure_exh"
            title = "Secure ExHentai/E-Hentai"
            summary = "Use the HTTPS version of ExHentai/E-Hentai."
        }

        listPreference {
            defaultValue = "auto"
            key = "ehentai_quality"
            summary = "The quality of the downloaded images"
            title = "Image quality"
            entries = arrayOf(
                    "Auto",
                    "2400x",
                    "1600x",
                    "1280x",
                    "980x",
                    "780x"
            )
            entryValues = arrayOf(
                    "auto",
                    "ovrs_2400",
                    "ovrs_1600",
                    "high",
                    "med",
                    "low"
            )
        }

        listPreference {
            title = "Search result count per page"
            summary = "Requires the 'Paging Enlargement' hath perk"
            defaultValue = "rc_0"
            key = "ex_search_size"
            entries = arrayOf(
                    "25 results",
                    "50 results",
                    "100 results",
                    "200 results"
            )
            entryValues = arrayOf(
                    "rc_0",
                    "rc_1",
                    "rc_2",
                    "rc_3"
            )
            dependency = "enable_exhentai"
        }

        listPreference {
            defaultValue = "tr_2"
            title = "Thumbnail rows"
            summary = "Affects loading speeds. It is recommended to set this to the maximum size your hath perks allow"
            key = "ex_thumb_rows"
            dependency = "enable_exhentai"
            entries = arrayOf(
                    "4",
                    "10 (requires 'More Thumbs' hath perk)",
                    "20 (requires 'Thumbs Up' hath perk)",
                    "40 (requires 'All Thumbs' hath perk)"
            )
            entryValues = arrayOf(
                    "tr_2",
                    "tr_5",
                    "tr_10",
                    "tr_20"
            )
        }

        preferenceCategory {
            title = "Advanced"
            isPersistent = false

            preference {
                title = "Migrate library metadata"
                isPersistent = false
                key = "ex_migrate_library"
                summary = "Fetch the library metadata to enable tag searching in the library. This button will be visible even if you have already fetched the metadata" />
            }
        }
    }

    private val preferences: PreferencesHelper by injectLazy()

    val enableExhentaiPref by lazy {
        findPreference("enable_exhentai") as SwitchPreference
    }

    val migrateLibraryPref by lazy {
        findPreference("ex_migrate_library") as Preference
    }

    val useJpTitlePref by lazy {
        findPreference("use_jp_title") as SwitchPreference
    }

    override fun onViewCreated(view: View, savedState: Bundle?) {
        super.onViewCreated(view, savedState)

        enableExhentaiPref.setOnPreferenceChangeListener { preference, newVal ->
            newVal as Boolean
            (activity as SettingsActivity).parentFlags = SettingsActivity.FLAG_EH_RECREATE
            if(!newVal) {
                preferences.enableExhentai().set(false)
                true
            } else {
                startActivity(Intent(context, LoginActivity::class.java))
                false
            }
        }

        migrateLibraryPref.setOnPreferenceClickListener {
            MetadataFetchDialog().askMigration(activity)
            true
        }

        useJpTitlePref.setOnPreferenceChangeListener { preference, any ->
            (activity as SettingsActivity).parentFlags = SettingsActivity.FLAG_EH_RECREATE
            true
        }
    }
}