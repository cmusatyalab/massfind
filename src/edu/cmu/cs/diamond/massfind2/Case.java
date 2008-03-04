package edu.cmu.cs.diamond.massfind2;


public class Case {
    final private CasePiece rightCC;

    final private CasePiece leftCC;

    final private CasePiece rightML;

    final private CasePiece leftML;

    final private String name;

    public Case(CasePiece rightCC, CasePiece leftCC, CasePiece rightML,
            CasePiece leftML, String name) {
        this.rightCC = rightCC;
        this.leftCC = leftCC;
        this.rightML = rightML;
        this.leftML = leftML;

        this.name = name;
    }

    public CasePiece getLeftCC() {
        return leftCC;
    }

    public CasePiece getLeftML() {
        return leftML;
    }

    public String getName() {
        return name;
    }

    public CasePiece getRightCC() {
        return rightCC;
    }

    public CasePiece getRightML() {
        return rightML;
    }

    public int getMaximumHeight() {
        return Math.max(rightCC.getImage().getHeight(), Math.max(leftCC
                .getImage().getHeight(), Math.max(rightML.getImage()
                .getHeight(), leftML.getImage().getHeight())));
    }
}
