module JavafxNetworking{
    requires javafx.controls;
    requires javafx.fxml;
    requires io.netty.all;

    opens com.hirshi001.javafxnetworking to javafx.fxml;
    exports com.hirshi001.javafxnetworking;
}