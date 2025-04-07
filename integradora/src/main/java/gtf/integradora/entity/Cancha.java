package gtf.integradora.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.mongodb.core.mapping.Field;

public class Cancha {

    @Field("nombreCancha")
    private String nombreCancha;

    public Cancha() {}

    @JsonCreator
    public Cancha(@JsonProperty("nombreCancha") String nombreCancha) {
        this.nombreCancha = nombreCancha;
    }

    public String getNombreCancha() {
        return nombreCancha;
    }

    public void setNombreCancha(String nombreCancha) {
        this.nombreCancha = nombreCancha;
    }

    @Override
    public String toString() {
        return "Cancha{" +
                "nombreCancha='" + nombreCancha + '\'' +
                '}';
    }
}