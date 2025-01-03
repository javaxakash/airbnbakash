package com.airbnb.repository;

import com.airbnb.entity.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PropertyRepository extends JpaRepository<Property, Long> {
    @Query("select p from Property p JOIN Location l on p.location=l.id JOIN Country c on p.country=c.id where l.locationName=:location or c.countryName=:location")
    List<Property> findPropertyByLocation(@Param("location") String locationName);

}