package no.uio.ifi.in2000.martirhe.appsolution.data.remote.metalert

import no.uio.ifi.in2000.martirhe.appsolution.model.metalert.MetAlertCollection
import no.uio.ifi.in2000.martirhe.appsolution.model.metalert.SimpleMetAlert
import javax.inject.Inject


interface MetAlertRepositoryInterface {
    suspend fun getMetAlerts(): MetAlertCollection
    suspend fun getSimpleMetAlerts(): List<SimpleMetAlert>

}

class MetAlertRepository @Inject constructor(
    private val dataSource: MetAlertDataSource
) : MetAlertRepositoryInterface {


    override suspend fun getMetAlerts(): MetAlertCollection {
        return dataSource.fetchMetAlerts()
    }

    override suspend fun getSimpleMetAlerts(): List<SimpleMetAlert> {

        val metAlertCollection = getMetAlerts()
        val simpleMetAlertList = mutableListOf<SimpleMetAlert>()
        var multiPolygon: List<List<List<List<Float>>>>

        metAlertCollection.features.forEach{

            try {
                multiPolygon = if (it.geometry.type == "Polygon") {
                    listOf(it.geometry.coordinates as List<List<List<Float>>>) // Warning: This cast is correct with expected json data
                } else {
                    it.geometry.coordinates as List<List<List<List<Float>>>> // Warning: This cast is correct with expected json data
                }
                val area: String = it.properties.area
                val awarenessLevel: String = it.properties.awarenessLevel
                val awarenessType: String = it.properties.awarenessType
                val consequences: String = it.properties.consequences
                val description: String = it.properties.description
                val eventAwarenessName: String = it.properties.eventAwarenessName
                simpleMetAlertList.add(SimpleMetAlert(
                    multiPolygon = multiPolygon,
                    area = area,
                    awarenessLevel = awarenessLevel,
                    awarenessType = awarenessType,
                    consequences = consequences,
                    description = description,
                    eventAwarenessName = eventAwarenessName))
            } catch (_: Exception) {

            }
        }

        return simpleMetAlertList
    }
}
