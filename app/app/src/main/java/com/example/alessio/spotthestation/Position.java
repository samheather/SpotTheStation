package com.example.alessio.spotthestation;

public class Position {

    private double radiansHeading;

    private double degsHeading;

    private double  userDistance;

    private double  elevation;

    public double getRadiansHeading() {

        return radiansHeading;

    }

    public void setRadiansHeading(double radiansHeading) {

        this.radiansHeading = radiansHeading;

    }

    public double getDegsHeading() {

        return degsHeading;

    }

    public void setDegsHeading(double degsHeading) {

        this.degsHeading = degsHeading;

    }

    public double getUserDistance() {

        return userDistance;

    }

    public void setUserDistance(double userDistance) {

        this.userDistance = userDistance;

    }

    public double getElevation() {

        return elevation;

    }

    public void setElevation(double elevation) {

        this.elevation = elevation;

    }


}