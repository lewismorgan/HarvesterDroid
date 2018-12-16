package com.lewisjmorgan.harvesterdroid.api.repository

abstract class CachedRepository<T: Any> {
  protected abstract val cache: MutableList<T>
}