package com.lewisjmorgan.harvesterdroid

interface IGalaxyProvider {
  /**
   * Provides a list of galaxies.
   * @return List<Galaxy>
   */
  fun provideGalaxies(): List<Galaxy>
}