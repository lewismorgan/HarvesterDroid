/**
 * Created by lewis on 11/11/17
 */
module io.github.waverunner.harvesterdroid.api {
  //requires javax.persistence;
  requires slf4j.api;
  requires bson4jackson;
  requires com.fasterxml.jackson.core;
  requires com.fasterxml.jackson.databind;
  requires com.fasterxml.jackson.dataformat.xml;
  requires java.xml;
  requires java.xml.bind;

  exports io.github.waverunner.harvesterdroid.api.resource;
  exports io.github.waverunner.harvesterdroid.api.xml;
  exports io.github.waverunner.harvesterdroid.api.tracker;
}