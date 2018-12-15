package com.lewisjmorgan.harvesterdroid

class ProviderDataStore: IGalaxyProvider, IGalaxyResourceProvider {
  private val galaxies = ArrayList<Galaxy>()
  private val resources: HashMap<Galaxy, ArrayList<GalaxyResource>> = HashMap()

  private var lastActiveDownloadTimestamp: Long = 0L

  override fun provideGalaxies(): List<Galaxy> {
    return galaxies
  }

  override fun provideCurrentGalaxyResources(galaxy: Galaxy): List<GalaxyResource> {
    // TODO Implement
    return resources.getOrDefault(galaxy, arrayListOf())
  }

  override fun provideGalaxyResource(galaxy: Galaxy, name: String): GalaxyResource? {
    val resources = this.resources.getOrDefault(galaxy, arrayListOf())

    return resources.firstOrNull { it.name == name }
  }

  fun replaceGalaxiesWith(galaxies: List<Galaxy>) {
    this.galaxies.clear()
    this.galaxies.addAll(galaxies)
  }

  fun insertResources(galaxy: Galaxy, resources: List<GalaxyResource>) {
    if (this.resources.containsKey(galaxy)) {
      val current = this.resources[galaxy]!!
      current.removeIf { res -> resources.any { res.name == it.name } }
      current.addAll(resources)
    } else {
      this.resources[galaxy] = ArrayList(resources)
    }
  }

  fun save() {
    // Saves galaxies and resources to appropriate files
    TODO("Implement")
  }
}