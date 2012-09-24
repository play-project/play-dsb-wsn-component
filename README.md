# play-dsb-wsn-component

The Service Bus WSN component.

## Description

The component gets the topics from the governance engine at startup. To retrieve the governance endpoint, it uses the registry service first; The registry service endpoint is defined in the play.cfg file (available in the classpath i.e. under $DSB_HOME/conf/) as *play.registry* property:

> play.registry=http://localhost:8080/registry/RegistryService