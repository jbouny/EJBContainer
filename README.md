EJBContainer
=====================

A basic implementation of an EJB Container (https://en.wikipedia.org/wiki/Enterprise_JavaBeans).
The implementation is write in Java and follow JEE specifications of EJB3.

Features:
- Classpath analyse: Map EJB implementations and interfaces
- Annotations: EJB / Local / PostConstruct / PreDestroy / Singleton / Stateful / Stateless / PersistenceContext
- Dynamic injection of EJB
- Method called on EJB pass by proxies which manage the transactions
- Dynamic injection of EntityManager
- Pool of EJB in order to improve performances
- Manage the cycle of life of beans
