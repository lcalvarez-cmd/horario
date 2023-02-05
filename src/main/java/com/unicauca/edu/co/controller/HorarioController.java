package com.unicauca.edu.co.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.unicauca.edu.co.dao.IHorarioDao;
import com.unicauca.edu.co.dao.IRecursoDao;
import com.unicauca.edu.co.model.dto.HorarioDto;
import com.unicauca.edu.co.model.projection.HorarioProjection;

@Controller
@RequestMapping("/horario")
public class HorarioController {
	
	@Autowired
	private IHorarioDao horarioDao;
	
	@Autowired
	private IRecursoDao recursoDao;
	
	@GetMapping("/vista/{recurso_id}")
	public String berHorario(@PathVariable Long recurso_id, Model modelo) {
		List<HorarioDto> listaLunes = horariosConfirmados(loqdevuelvelaConsultaPorjectionADto("LUNES",recurso_id));
		List<HorarioDto> listaMartes = horariosConfirmados(loqdevuelvelaConsultaPorjectionADto("MARTES",recurso_id));
		List<HorarioDto> listaMiercoles = horariosConfirmados(loqdevuelvelaConsultaPorjectionADto("MIERCOLES",recurso_id));
		List<HorarioDto> listaJueves = horariosConfirmados(loqdevuelvelaConsultaPorjectionADto("JUEVES",recurso_id));
		List<HorarioDto> listaViernes = horariosConfirmados(loqdevuelvelaConsultaPorjectionADto("VIERNES",recurso_id));
		List<HorarioDto> listaSabado = horariosConfirmados(loqdevuelvelaConsultaPorjectionADto("SABADO",recurso_id));
		String[] horasImpares = {"7:00 AM <br> 8:00 AM","9:00 AM <br> 10:00 AM","11:00 AM <br> 12:00 PM","02:00 PM <br> 03:00 PM","04:00 PM <br> 05:00 PM","06:00 PM <br> 07:00 PM","08:00 PM <br> 10:00 PM"};
		modelo.addAttribute("lunes",listaLunes);
		modelo.addAttribute("martes",listaMartes);
		modelo.addAttribute("miercoles",listaMiercoles);
		modelo.addAttribute("jueves",listaJueves);
		modelo.addAttribute("viernes",listaViernes);
		modelo.addAttribute("sabado",listaSabado);
		modelo.addAttribute("recurso",recursoDao.findById(recurso_id).get());
		modelo.addAttribute("horas", horasImpares);
		return "horario/listhorario";
	}
	
	//metodo para realizar la consulta con projection y devuelva ya una lista DTO
	private List<HorarioDto> loqdevuelvelaConsultaPorjectionADto(String dia, Long recurso_id){
		List<HorarioProjection> hprojection = horarioDao.listaHorarioPorDiaDeUnRecursos(dia, recurso_id);
		if(hprojection.size() == 0) {
			hprojection = new ArrayList<HorarioProjection>();
		}
		List<HorarioDto> listaHDto = new ArrayList<HorarioDto>();
		for (HorarioProjection h : hprojection) {
			HorarioDto objHorario = new HorarioDto(h.getCur_id(),h.getHor_dia(),h.getHor_hora_inicio(),h.getHor_hora_fin(),h.getRec_id(),h.getCur_id(),h.getAsig_nombre(),h.getCur_nombre(),h.getAsig_color());
			listaHDto.add(objHorario);
		}
		return listaHDto;
	}
	
	//metodo que recibe un horario de un dia y si no tiene horas asignadas ese día le asigna vacias 
	private List<HorarioDto> horariosConfirmados(List<HorarioDto> listaHorarios){//tomo la lista de horarios que me trae la bd
		List<HorarioDto> listEnviar = new ArrayList<>();
		String[] horas = {"07:00:00","09:00:00","11:00:00","14:00:00","16:00:00","18:00:00","20:00:00"};
		boolean esta = false;
		for (String hora : horas) {
			if(listaHorarios.size() > 0 ) {
				esta = false;
				for (HorarioDto hDto : listaHorarios) {
					if(hora.equals(hDto.getHor_hora_inicio())) {
						listEnviar.add(hDto);
						listaHorarios.remove(hDto);
						esta = true;
						break;
					}else {
						esta = false;
					}
				}
				if(esta == false) {
					HorarioDto newHorariodto = new HorarioDto(null, "", hora, null, null,null, " ", " ",null);
					listEnviar.add(newHorariodto);
				}
			}
			else {
				HorarioDto newHorariodto = new HorarioDto(null, "", hora, null, null,null, " ", " ",null);
				listEnviar.add(newHorariodto);
			}
			
		}
		return listEnviar;
	}
}
