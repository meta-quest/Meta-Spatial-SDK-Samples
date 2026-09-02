package com.meta.pixelandtexel.scanner.android.datasource.repository

import com.meta.pixelandtexel.scanner.datasource.network.SmartHomeApi
import com.meta.pixelandtexel.scanner.android.datasource.mapper.DeviceMapper
import com.meta.pixelandtexel.scanner.android.datasource.mapper.DomainMapper
import com.meta.pixelandtexel.scanner.android.domain.repository.SmartHomeRepository
import com.meta.pixelandtexel.scanner.models.devices.Device
import com.meta.pixelandtexel.scanner.models.devices.ThingEntity
import com.meta.pixelandtexel.scanner.models.devices.domain.SensorDomain

class SmartHomeRepositoryImpl (
    private val api: SmartHomeApi
) : SmartHomeRepository {


    override suspend fun getDevices(): List<Device> {
        val template = """
            {%- set javi_entities = states | selectattr("entity_id", "search") | map(attribute="entity_id") | list -%}
            {% set devices = javi_entities | map("device_id") | unique | reject("eq", None) | list -%}
            {% set ns = namespace(devices=[]) -%}
            {% for device in devices -%}
              {% set entities = device_entities(device) | list -%}
              {% if entities -%}
                {% set device_name = device_attr(device, "name_by_user") or device_attr(device, "name") or device -%}
                {% set ns.devices = ns.devices + [{"name": device_name, "entities": entities | sort}] -%}
              {% endif -%}
            {% endfor -%}
            {{ {"devices": ns.devices} | to_json }}

        """.trimIndent()

        try {
            val response = api.postTemplate(
                body = mapOf("template" to template)
            )

            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    return DeviceMapper.map(responseBody)
                }
            }

            return emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            return emptyList()
        }
    }

    override suspend fun getThingEntities(thingEntities: List<ThingEntity>): List<ThingEntity> {
        try {
            val updatedEntities = thingEntities.map { entity ->
                val response = api.getEntityState(entity.id)
                val responseBody = response.body() ?: return@map entity

                var newState = responseBody.state
                if (entity.domain is SensorDomain) {
                    val unit = responseBody.attributes?.unitOfMeasurement ?: ""
                    newState = "$newState $unit"
                }

                entity.copy(
                    domain = DomainMapper.fromOtherDomainNewValue(
                        entity.domain,
                        newState,
                        responseBody.attributes
                    )
                )
            }
            return updatedEntities
        } catch (e: Exception) {
            e.printStackTrace()
            return emptyList()
        }

    }

    override suspend fun getActionForThing(
        thingId: String,
        action: String,
        newValue: Pair<String, Any>?
    ): Boolean {
        return try {
            val thingDomain = thingId.substringBefore(".", missingDelimiterValue = "")
            val service = action.lowercase()
            val bodyMap = mutableMapOf<String, Any>("entity_id" to thingId)
            if (newValue != null) {
                bodyMap[newValue.first] = newValue.second
            }


            val response = api.postActionToDeviceDomain(
                device = thingDomain,
                action = service,
                body = bodyMap.toMap()
            )

            response.isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }

    }
}