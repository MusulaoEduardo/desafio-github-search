package br.com.igorbag.githubsearch.domain

import com.google.gson.annotations.SerializedName

data class Repository(
    val name: String,
    @SerializedName("html_url")
    val htmlUrl: String
) {

    val repositories: CharSequence? = null

    fun displayDetails(): String {
        return "Repository: $name\nURL: $htmlUrl"
    }

}
