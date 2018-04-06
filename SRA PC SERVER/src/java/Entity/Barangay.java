package Entity;

public class Barangay {
    private String barangay, municipality;
    
    public Barangay (String municipality, String barangay){
        this.barangay = barangay;
        this.municipality = municipality;
    }
    

    /**
     * @return the barangay
     */
    public String getBarangay() {
        return barangay;
    }

    /**
     * @param barangay the barangay to set
     */
    public void setBarangay(String barangay) {
        this.barangay = barangay;
    }

    /**
     * @return the municipality
     */
    public String getMunicipality() {
        return municipality;
    }

    /**
     * @param municipality the municipality to set
     */
    public void setMunicipality(String municipality) {
        this.municipality = municipality;
    }
}
