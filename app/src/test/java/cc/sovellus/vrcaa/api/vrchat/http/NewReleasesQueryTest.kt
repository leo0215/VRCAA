package cc.sovellus.vrcaa.api.vrchat.http

import org.junit.Assert.assertEquals
import org.junit.Test

class NewReleasesQueryTest {
    @Test
    fun newReleasesRequestUsesPublicationDateDescendingPublic() {
        assertEquals(
            "https://api.vrchat.cloud/api/1/worlds?featured=false&sort=publicationDate&order=descending&releaseStatus=public&n=20",
            newReleasesWorldsUrl("https://api.vrchat.cloud/api/1")
        )
    }
}
