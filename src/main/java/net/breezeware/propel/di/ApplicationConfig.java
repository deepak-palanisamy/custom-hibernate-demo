package net.breezeware.propel.di;

import net.breezeware.propel.annotation.Component;
import net.breezeware.propel.annotation.ComponentScan;
import net.breezeware.propel.annotation.Configuration;

@Configuration
@ComponentScan(packageNames = {"net.breezeware.propel.di", "net.breezeware.propel.dao", "net.breezeware.propel.service.impl"})
public class ApplicationConfig {
}
