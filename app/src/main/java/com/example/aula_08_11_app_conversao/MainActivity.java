package com.example.aula_08_11_app_conversao;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.aula_08_11_app_conversao.api.ApiCallback;
import com.example.aula_08_11_app_conversao.api.AwesomeClient;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.Currency;

public class MainActivity extends AppCompatActivity {

    String opcaoSelecionada = "";
    Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        RadioGroup rg = findViewById(R.id.opcoes);
        rg.setOnCheckedChangeListener((radioGroup, i) -> {
            RadioButton rb = findViewById(i);
            switch (rb.getText().toString()) {
                case "Real -> Dolar":
                    opcaoSelecionada = "BRL-USD";
                    break;
                case "Real -> Euro":
                    opcaoSelecionada = "BRL-EUR";
                    break;
                case "Dolar -> Euro":
                    opcaoSelecionada = "USD-EUR";
                    break;
                case "Euro -> Libra":
                    opcaoSelecionada = "EUR-GBP";
                    break;
                default:
                    break;
            }
        });


        Button bt = findViewById(R.id.converter);
        bt.setOnClickListener(view -> converter());
    }


    private void converter() {
        if (opcaoSelecionada.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Selecione uma opção!", Toast.LENGTH_LONG).show();
            return;
        }

        EditText valorEt = findViewById(R.id.valor);
        String valorS = valorEt.getText().toString();
        if (valorS.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Digite um valor!", Toast.LENGTH_LONG).show();
            return;
        }

        try {
            AwesomeClient awesomeClient = new AwesomeClient();

            awesomeClient.buscarDados(opcaoSelecionada, new ApiCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    CurrencyRate currencyRate = null;
                    JsonObject jsonObject = JsonParser.parseString(result).getAsJsonObject();
                    if (jsonObject.has("BRLUSD")) {
                        currencyRate = gson.fromJson(jsonObject.get("BRLUSD"), CurrencyRate.class);
                    }else if (jsonObject.has("BRLEUR")) {
                        currencyRate = gson.fromJson(jsonObject.get("BRLEUR"), CurrencyRate.class);
                    }else if (jsonObject.has("USDEUR")) {
                        currencyRate = gson.fromJson(jsonObject.get("USDEUR"), CurrencyRate.class);
                    }else if (jsonObject.has("EURGBP")) {
                        currencyRate = gson.fromJson(jsonObject.get("EURGBP"), CurrencyRate.class);
                    }else {
                        Toast.makeText(getApplicationContext(), "Não foi possível encontrar o valor da moeda", Toast.LENGTH_SHORT).show();
                    }

                    if (currencyRate != null) {
                        Double valor = Double.valueOf(valorS);

                        TextView resultado = findViewById(R.id.resultado);
                        Double valorMoeda = Double.valueOf(currencyRate.getHigh());
                        resultado.setText(String.valueOf(valor * valorMoeda));
                    }
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Log.e("Erro", "Ocorreu um erro", e);
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }
}








