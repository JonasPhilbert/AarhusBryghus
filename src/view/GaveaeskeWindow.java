package view;

import java.util.ArrayList;
import controller.Controller;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.Gaveaeske;
import model.GaveaeskePakning;
import model.Produkt;
import storage.Storage;

public class GaveaeskeWindow extends Stage {

	private Gaveaeske gaveæske;
	
	private ListView<Produkt> lvwProdukter, lvwTilføjedeProdukter;
	private Button btnTilføj, btnFjern, btnAccepter, btnAnnuller;
	private Label lblPris;
	private ToggleGroup tggPakning = new ToggleGroup();
	private RadioButton rbPakningGaveæske, rbPakningTrækasse, rbPakningGavekurv, rbPakningPapkasse;

	public GaveaeskeWindow(String title, Stage owner) {
		this.initOwner(owner);
		this.initStyle(StageStyle.DECORATED);
		this.initModality(Modality.APPLICATION_MODAL);
		this.setOnCloseRequest(e -> btnAnnullerAction());
		this.setMinHeight(100);
		this.setMinWidth(200);
		this.setResizable(false);
		
		this.setTitle(title);
		GridPane pane = new GridPane();
		this.initContent(pane);

		Scene scene = new Scene(pane);
		this.setScene(scene);
		scene.getStylesheets().add("style_jbootx.css");
	}

	private void setUpPane(GridPane pane) {
		pane.setPadding(new Insets(20));
		pane.setHgap(20);
		pane.setVgap(10);
	}

	public void initContent(GridPane pane) {
		setUpPane(pane);

		// Column 0
		ViewHelper.label(pane, 0, 0, "Tilgængelige produkter:");
		
		lvwProdukter = new ListView<>();
		pane.add(lvwProdukter, 0, 1, 1, 8);

		// Column 1
		btnTilføj = new Button("→");
		btnTilføj.setOnAction(e -> btnTilføjAction());
		pane.add(btnTilføj, 1, 5);

		btnFjern = new Button("←");
		btnFjern.setOnAction(e -> btnFjernAction());
		pane.add(btnFjern, 1, 6);

		// Column 2
		ViewHelper.label(pane, 2, 0, "Produkter i gaveæske:");

		lvwTilføjedeProdukter = new ListView<>();
		pane.add(lvwTilføjedeProdukter, 2, 1, 1, 8);
		
		lblPris = ViewHelper.label(pane, 2, 9, "PRIS: 0.0 kr.");
		lblPris.setStyle("-fx-font-size: 16;\n-fx-font-family: monospace;");
		
		// Column 3
		ViewHelper.label(pane, 3, 0, "Gaveæske pakning/emballage:");
		
		rbPakningGaveæske = new RadioButton("Gaveæske");
		rbPakningGaveæske.setUserData(GaveaeskePakning.Gaveæske);
		rbPakningGaveæske.setToggleGroup(tggPakning);
		rbPakningGaveæske.setOnAction(e -> radioBtnAction());
		pane.add(rbPakningGaveæske, 3, 1);
		
		rbPakningTrækasse = new RadioButton("Trækasse");
		rbPakningTrækasse.setUserData(GaveaeskePakning.Trækasse);
		rbPakningTrækasse.setToggleGroup(tggPakning);
		rbPakningTrækasse.setOnAction(e -> radioBtnAction());
		pane.add(rbPakningTrækasse, 3, 2);
		
		rbPakningGavekurv = new RadioButton("Gavekurv");
		rbPakningGavekurv.setUserData(GaveaeskePakning.Gavekurv);
		rbPakningGavekurv.setToggleGroup(tggPakning);
		rbPakningGavekurv.setOnAction(e -> radioBtnAction());
		pane.add(rbPakningGavekurv, 3, 3);
		
		rbPakningPapkasse = new RadioButton("Papkasse");
		rbPakningPapkasse.setUserData(GaveaeskePakning.Papkasse);
		rbPakningPapkasse.setToggleGroup(tggPakning);
		rbPakningPapkasse.setOnAction(e -> radioBtnAction());
		pane.add(rbPakningPapkasse, 3, 4);
		
		btnAccepter = new Button("Accepter");
		btnAccepter.setOnAction(e -> btnAccepterAction());
		pane.add(btnAccepter, 3, 5);

		btnAnnuller = new Button("Annuller");
		btnAnnuller.setOnAction(e -> btnAnnullerAction());
		pane.add(btnAnnuller, 3, 6);
		
		updateLvwProdukter();
		
		gaveæske = Controller.createGaveaeske();
	}

	private void updateLvwProdukter() {
		lvwProdukter.getItems().removeAll(lvwProdukter.getItems());
		ArrayList<Produkt> produkter = new ArrayList<>();
		for (Produkt p : Controller.getProdukterIPrisKategori(Storage.getButikPrisKategori())) {
			if (p.getProduktKategori() == Storage.getFlaskeølProduktKategori() || p.getProduktKategori() == Storage.getGlasProduktKategori()) {
				produkter.add(p);
			}
		}
		lvwProdukter.getItems().addAll(produkter);
	}
	
	private void btnTilføjAction() {
		if (ViewHelper.listViewHasSelected(lvwProdukter)) {
			Produkt selected = lvwProdukter.getSelectionModel().getSelectedItem();
			lvwTilføjedeProdukter.getItems().add(selected);
			gaveæske.addProdukt(selected);
			updateTotalPris();
		}
	}

	private void btnFjernAction() {
		if (ViewHelper.listViewHasSelected(lvwTilføjedeProdukter)) {
			Produkt selected = lvwTilføjedeProdukter.getSelectionModel().getSelectedItem();
			lvwTilføjedeProdukter.getItems().remove(selected);
			gaveæske.removeProdukt(selected);
			updateTotalPris();
		}
	}
	
	private void radioBtnAction() {
		Controller.setGaveaeskePakning(gaveæske, (GaveaeskePakning) tggPakning.getSelectedToggle().getUserData());
		updateTotalPris();
	}
	
	private void btnAccepterAction() {
		this.close();
	}
	
	private void updateTotalPris() {
		lblPris.setText(String.format("TOTAL: %.2f kr.", getGaveæske().getPris()));
	}
	
	private void btnAnnullerAction() {
		gaveæske = null;
		this.close();
	}
	
	public Gaveaeske getGaveæske() {
		return gaveæske;
	}
}