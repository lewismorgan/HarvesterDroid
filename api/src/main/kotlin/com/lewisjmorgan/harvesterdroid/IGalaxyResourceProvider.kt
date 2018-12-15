package com.lewisjmorgan.harvesterdroid

interface IGalaxyResourceProvider {
  /**
   * Provides a list of currently obtainable resources.
   * @param galaxy Galaxy that should have its resource data downloaded
   * @return List<GalaxyResource>
   */
  fun provideCurrentGalaxyResources(galaxy: Galaxy): List<GalaxyResource>
  fun provideGalaxyResource(galaxy: Galaxy, name: String): GalaxyResource?
}