import { Prognose } from '../prognose';
import { ModeloExclusivo } from '../modelo-exclusivo';
export class Avaliacao {
    constructor(
        public id?: number,
        public nome?: string,
        public status?: number,
        public codigo?: string,
        public resultado?: string,
        public prognose?: Prognose,
        public modelosavaliados?: ModeloExclusivo,
    ) {
    }
}
