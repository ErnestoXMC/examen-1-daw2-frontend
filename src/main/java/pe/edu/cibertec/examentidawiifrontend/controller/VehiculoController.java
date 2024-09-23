package pe.edu.cibertec.examentidawiifrontend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import pe.edu.cibertec.examentidawiifrontend.dto.AutoRequestDTO;
import pe.edu.cibertec.examentidawiifrontend.dto.AutoResponseDTO;
import pe.edu.cibertec.examentidawiifrontend.viewModel.VehiculoModel;

@Controller
@RequestMapping("/vehiculo")
public class VehiculoController {

    @Autowired
    RestTemplate restTemplate;

    @GetMapping("/inicio")
    public String inicio(Model model){
        VehiculoModel vehiculoModel = new VehiculoModel("00", "", "", "", "", "", "");
        model.addAttribute("vehiculoModel", vehiculoModel);
        return "inicio";
    }

    @PostMapping("/buscar")
    public String buscar(@RequestParam("placa") String placa, Model model){
        //Validar los campos de entrada
        if(placa == null || placa.trim().length() == 0){
            VehiculoModel vehiculoModel = new VehiculoModel("01", "Debe Ingresar una Placa Correcta", "", "", "", "", "");
            model.addAttribute("vehiculoModel", vehiculoModel);
            return "inicio";
        }
        if(placa.trim().length() > 8 || placa.trim().length() < 7){
            VehiculoModel vehiculoModel = new VehiculoModel("01", "Debe Ingresar una Placa Correcta", "", "", "", "", "");
            model.addAttribute("vehiculoModel", vehiculoModel);
            return "inicio";
        }
        try {
            //Invocar Appi de validacion de usuario
            String endpoint = "http://localhost:8081/auto/datos";
            AutoRequestDTO autoRequestDTO = new AutoRequestDTO(placa);
            AutoResponseDTO autoResponseDTO = restTemplate.postForObject(endpoint, autoRequestDTO, AutoResponseDTO.class);

            //Validar Respuesta
            if(autoResponseDTO.codigo().equals("00")){
                VehiculoModel vehiculoModel = new VehiculoModel("00", "",
                        autoResponseDTO.marca(),
                        autoResponseDTO.modelo(),
                        autoResponseDTO.numeroAsiento(),
                        autoResponseDTO.precio(),
                        autoResponseDTO.color());
                model.addAttribute("vehiculoModel", vehiculoModel);
                return "resultado";
            }else{
                VehiculoModel vehiculoModel = new VehiculoModel("02", autoResponseDTO.mensaje(), "", "", "", "", "");
                model.addAttribute("vehiculoModel", vehiculoModel);
                return "inicio";
            }
        } catch (Exception e) {
            VehiculoModel vehiculoModel = new VehiculoModel("99", "Error: Ocurrio un problema con el servidor externo", "", "", "", "", "");
            model.addAttribute("vehiculoModel", vehiculoModel);
            System.out.println(e.getMessage());
            return "inicio";
        }
    }
}
