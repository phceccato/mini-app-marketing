import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { GeminiService } from '../services/gemini.service';

@Component({
  selector: 'app-review',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './review.component.html',
  styleUrls: ['./review.component.css']
})
export class ReviewComponent {
  @Input() data: any[] = [];
  @Input() dataId: string = '';
  @Output() aoFinalizar = new EventEmitter<void>();

  excelId: string = '';
  gerando = false;
  erro = '';

  constructor(
    private http: HttpClient,
    private geminiService: GeminiService
  ) {}

  baixarExcel(): void {
    if (!this.dataId || this.data.length === 0) {
      this.erro = 'Sem dados para gerar e baixar Excel.';
      return;
    }

    this.gerando = true;
    this.erro = '';

    this.geminiService.reviewData(this.dataId, this.data).subscribe({
      next: (res) => {
        const excelId = res.excelId;
        this.geminiService.downloadExcel(excelId).subscribe(blob => {
          const url = window.URL.createObjectURL(blob);
          const a = document.createElement('a');
          a.href = url;
          a.download = 'recibo.xlsx';
          a.click();
          window.URL.revokeObjectURL(url);
          this.gerando = false;

          // 🔥 Emite o evento somente após o download
          this.aoFinalizar.emit();
        });
      },
      error: () => {
        this.erro = 'Erro ao gerar ou baixar o Excel.';
        this.gerando = false;
      }
    });
  }
}
