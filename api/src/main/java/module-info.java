/**
 * Created by lewis on 11/11/17
 */
module io.github.waverunner.harvesterdroid.api {
  requires javax.persistence;
  requires java.xml.bind;
  requires slf4j.api;
  requires bson4jackson;
  requires jackson.core;
  requires jackson.databind;
  exports io.github.waverunner.harvesterdroid.api.resource;
  exports io.github.waverunner.harvesterdroid.api.xml;
  exports io.github.waverunner.harvesterdroid.api.tracker;
}