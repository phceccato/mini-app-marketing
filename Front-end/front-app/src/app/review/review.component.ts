import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { GeminiService } from '../services/gemini.service';

@Component({
  selector: 'app-review', 
  standalone: true, // Componente pode ser usado de forma independente
  imports: [CommonModule, FormsModule],
  templateUrl: './review.component.html', 
  styleUrls: ['./review.component.css'] 
})
export class ReviewComponent {
  @Input() data: any[] = []; 
  @Input() dataId: string = '';
  @Output() aoFinalizar = new EventEmitter<void>(); 

  excelId: string = ''; // ID do arquivo Excel
  gerando = false; 
  erro = ''; // Mensagem de erro

  // Injeção de dependências
  constructor(
    private http: HttpClient,
    private geminiService: GeminiService
  ) {}

  // Função para baixar o Excel com os dados revisados
  baixarExcel(): void {
    // Altera 'nomeProdutor' e 'tipoCultura' para maiúsculas antes de enviar para o backend
    this.data.forEach(item => {
      item.nomeProdutor = item.nomeProdutor?.toUpperCase();
      item.tipoCultura = item.tipoCultura?.toUpperCase();
    });

    // Verifica se tem algum dado ou se o 'dataId' está vazio
    if (!this.dataId || this.data.length === 0) {
      this.erro = 'Sem dados para gerar e baixar Excel.';
      return; 
    }

    this.gerando = true;
    this.erro = ''; 

    // Chama o serviço para revisar os dados
    this.geminiService.reviewData(this.dataId, this.data).subscribe({
      next: (res) => {
        const excelId = res.excelId;
        // Serviço para baixar o Excel com base no ID
        this.geminiService.downloadExcel(excelId).subscribe(blob => {
          const url = window.URL.createObjectURL(blob);
          const a = document.createElement('a');
          a.href = url;
          a.download = 'recibo.xlsx';
          a.click(); // Aciona o download
          window.URL.revokeObjectURL(url);
          this.gerando = false; 
          this.aoFinalizar.emit(); 
        });
      },
      // Caso ocorra um erro
      error: () => {
        this.erro = 'Erro ao gerar ou baixar o Excel.'; 
        this.gerando = false;
      }
    });
  }  
}