import Enums.Enums;

public class mushroomRecord {
    private char edibility; // 'e' for edible, 'p' for poisonous

    // Attributes
    private Enums.CapShape capShape;
    private Enums.CapSurface capSurface;
    private Enums.CapColor capColor;
    private Enums.Bruises bruises;
    private Enums.Odor odor;
    private Enums.GillAttachment gillAttachment;
    private Enums.GillSpacing gillSpacing;
    private Enums.GillSize gillSize;
    private Enums.GillColor gillColor;
    private Enums.StalkShape stalkShape;
    private Enums.StalkRoot stalkRoot;
    private Enums.StalkSurface stalkSurfaceAboveRing;
    private Enums.StalkSurface stalkSurfaceBelowRing;
    private Enums.StalkColor stalkColorAboveRing;
    private Enums.StalkColor stalkColorBelowRing;
    private Enums.VeilType veilType;
    private Enums.VeilColor veilColor;
    private Enums.RingNumber ringNumber;
    private Enums.RingType ringType;
    private Enums.SporePrintColor sporePrintColor;
    private Enums.Population population;
    private Enums.Habitat habitat;

    // Constructor
    public mushroomRecord(char edibility, Enums.CapShape capShape, Enums.CapSurface capSurface,
                          Enums.CapColor capColor, Enums.Bruises bruises, Enums.Odor odor,
                          Enums.GillAttachment gillAttachment, Enums.GillSpacing gillSpacing,
                          Enums.GillSize gillSize, Enums.GillColor gillColor, Enums.StalkShape stalkShape,
                          Enums.StalkRoot stalkRoot, Enums.StalkSurface stalkSurfaceAboveRing,
                          Enums.StalkSurface stalkSurfaceBelowRing, Enums.StalkColor stalkColorAboveRing,
                          Enums.StalkColor stalkColorBelowRing, Enums.VeilType veilType,
                          Enums.VeilColor veilColor, Enums.RingNumber ringNumber, Enums.RingType ringType,
                          Enums.SporePrintColor sporePrintColor, Enums.Population population,
                          Enums.Habitat habitat) {
        this.edibility = edibility;
        this.capShape = capShape;
        this.capSurface = capSurface;
        this.capColor = capColor;
        this.bruises = bruises;
        this.odor = odor;
        this.gillAttachment = gillAttachment;
        this.gillSpacing = gillSpacing;
        this.gillSize = gillSize;
        this.gillColor = gillColor;
        this.stalkShape = stalkShape;
        this.stalkRoot = stalkRoot;
        this.stalkSurfaceAboveRing = stalkSurfaceAboveRing;
        this.stalkSurfaceBelowRing = stalkSurfaceBelowRing;
        this.stalkColorAboveRing = stalkColorAboveRing;
        this.stalkColorBelowRing = stalkColorBelowRing;
        this.veilType = veilType;
        this.veilColor = veilColor;
        this.ringNumber = ringNumber;
        this.ringType = ringType;
        this.sporePrintColor = sporePrintColor;
        this.population = population;
        this.habitat = habitat;
    }

}
