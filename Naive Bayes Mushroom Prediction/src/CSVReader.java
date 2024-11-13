import Enums.Enums;
import java.util.*;
import java.io.*;

public class CSVReader {

    private String filepath;

    public CSVReader(String filepath) {
        this.filepath = filepath;
    }

    public List<mushroomRecord> read() {
        List<mushroomRecord> dataset = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filepath));
            String line = reader.readLine(); // Read and skip the header line

            while ((line = reader.readLine()) != null) {
                String[] attributes = line.split(",");
                if (attributes.length != 23) {
                    throw new IllegalArgumentException("Invalid data: " + line);
                }


                mushroomRecord record = new mushroomRecord();
                dataset.add(record);
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return dataset;
    }

    private Enums.CapShape mapCapShape(String value) {
        switch (value) {
            case "b":
                return Enums.CapShape.BELL;
            case "c":
                return Enums.CapShape.CONICAL;
            case "x":
                return Enums.CapShape.CONVEX;
            case "f":
                return Enums.CapShape.FLAT;
            case "k":
                return Enums.CapShape.KNOBBED;
            case "s":
                return Enums.CapShape.SUNKEN;
            default:
                throw new IllegalArgumentException("Unknown cap shape value: " + value);
        }
    }

    private Enums.Bruises mapBruises(String value) {
        return value.equals("t") ? Enums.Bruises.YES : Enums.Bruises.NO;
    }

    private Enums.CapColor mapColor(String value) {
            switch (value) {
            case "n":
                return Enums.CapColor.BROWN;
            case "b":
                return Enums.CapColor.BUFF;
            case "c":
                return Enums.CapColor.CINNAMON;
            case "g":
                return Enums.CapColor.GRAY;
            case "r":
                return Enums.CapColor.GREEN;
            case "p":
                return Enums.CapColor.PINK;
            case "u":
                return Enums.CapColor.PURPLE;
            case "e":
                return Enums.CapColor.RED;
            case "w":
                return Enums.CapColor.WHITE;
            case "y":
                return Enums.CapColor.YELLOW;
            default:
                throw new IllegalArgumentException("Unknown cap color value: " + value);
        }
    }

    private Enums.CapSurface mapCapSurface(String value) {
        switch (value) {
            case "f":
                return Enums.CapSurface.FIBROUS;
            case "g":
                return Enums.CapSurface.GROOVES;
            case "y":
                return Enums.CapSurface.SCALY;
            case "s":
                return Enums.CapSurface.SMOOTH;
            default:
                throw new IllegalArgumentException("Unknown cap surface value: " + value);
        }
    }

    private Enums.GillAttachment mapGillAttachment(String value) {
        switch (value) {
            case "a":
                return Enums.GillAttachment.ATTACHED;
            case "d":
                return Enums.GillAttachment.DESCENDING;
            case "f":
                return Enums.GillAttachment.FREE;
            case "n":
                return Enums.GillAttachment.NOTCHED;
            default:
                throw new IllegalArgumentException("Unknown gill attachment value: " + value);
        }
    }

    private Enums.GillColor mapGillColour(String value) {
        switch (value) {
            case "k":
                return Enums.GillColor.BLACK;
            case "n":
                return Enums.GillColor.BROWN;
            case "b":
                return Enums.GillColor.BUFF;
            case "h":
                return Enums.GillColor.CHOCOLATE;
            case "g":
                return Enums.GillColor.GRAY;
            case "r":
                return Enums.GillColor.GREEN;
            case "o":
                return Enums.GillColor.ORANGE;
            case "p":
                return Enums.GillColor.PINK;
            case "u":
                return Enums.GillColor.PURPLE;
            case "e":
                return Enums.GillColor.RED;
            case "w":
                return Enums.GillColor.WHITE;
            case "y":
                return Enums.GillColor.YELLOW;
            default:
                throw new IllegalArgumentException("Unknown gill color value: " + value);
        }
    }

    private Enums.GillSize mapGillSize(String value) {
        return value.equals("b") ? Enums.GillSize.BROAD : Enums.GillSize.NARROW;
    }

    private Enums.GillSpacing mapGillSpacing(String value) {
        switch (value) {
            case "c":
                return Enums.GillSpacing.CLOSE;
            case "w":
                return Enums.GillSpacing.CROWDED;
            case "d":
                return Enums.GillSpacing.DISTANT;
            default:
                throw new IllegalArgumentException("Unknown gill spacing value: " + value);
        }
    }

    private Enums.Habitat mapHabitat(String value) {
        switch (value) {
            case "g":
                return Enums.Habitat.GRASSES;
            case "l":
                return Enums.Habitat.LEAVES;
            case "m":
                return Enums.Habitat.MEADOWS;
            case "p":
                return Enums.Habitat.PATHS;
            case "u":
                return Enums.Habitat.URBAN;
            case "w":
                return Enums.Habitat.WASTE;
            case "d":
                return Enums.Habitat.WOODS;
            default:
                throw new IllegalArgumentException("Unknown habitat value: " + value);
        }
    }

    private Enums.Odor mapOdor(String value) {
        switch (value) {
            case "a":
                return Enums.Odor.ANISE;
            case "l":
                return Enums.Odor.CREOSOTE;
            case "c":
                return Enums.Odor.FISHY;
            case "y":
                return Enums.Odor.FOUL;
            case "m":
                return Enums.Odor.MUSTY;
            case "n":
                return Enums.Odor.NONE;
            case "p":
                return Enums.Odor.PUNGENT;
            default:
                throw new IllegalArgumentException("Unknown odor value: " + value);
        }
    }

    private Enums.Population mapPopulation(String value) {
        switch (value) {
            case "a":
                return Enums.Population.ABUNDANT;
            case "c":
                return Enums.Population.CLUSTERED;
            case "n":
                return Enums.Population.NUMEROUS;
            case "s":
                return Enums.Population.SCATTERED;
            case "v":
                return Enums.Population.SEVERAL;
            case "y":
                return Enums.Population.SOLITARY;
            default:
                throw new IllegalArgumentException("Unknown population value: " + value);
        }
    }

    private Enums.RingType mapRingType(String value) {
        switch (value) {
            case "c":
                return Enums.RingType.COBWEBBY;
            case "e":
                return Enums.RingType.EVANESCENT;
            case "f":
                return Enums.RingType.FLARING;
            case "l":
                return Enums.RingType.LARGE;
            case "n":
                return Enums.RingType.NONE;
            case "p":
                return Enums.RingType.PENDANT;
            case "s":
                return Enums.RingType.SHEATHING;
            case "z":
                return Enums.RingType.ZONE;
            default:
                throw new IllegalArgumentException("Unknown ring type value: " + value);
        }
    }


}