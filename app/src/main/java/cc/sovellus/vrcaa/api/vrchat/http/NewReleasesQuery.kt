package cc.sovellus.vrcaa.api.vrchat.http

fun newReleasesWorldsUrl(baseUrl: String, n: Int = 20): String {
    return "$baseUrl/worlds?featured=false&sort=publicationDate&order=descending&releaseStatus=public&n=$n"
}
