BEGIN{
	delta=0.001;
	t=0.0;
	OFFSET["FEF1"]=0.100
	OFFSET["F005"]=0.100
	OFFSET["F004"]=0.020
	OFFSET["F003"]=0.050
	OFFSET["FEF2"]=0.100
}
{
	if(NR > 1){
		pgn=$3
		skip=length(pgn)-5
		if (skip>0){
		    pgn=substr(pgn, skip, 4)
		}
		offset=OFFSET[pgn]

		if (offset != 0){
			time=TIME[pgn]
			if ( (time+offset) > t  && (t != 0.0) ){
				t=(time+offset)
			}
			printf("(%f) can0 %08s#%s%s%s%s%s%s%s%s\n", t,$3,$5,$6,$7,$8,$9,$10,$11,$12);
			TIME[pgn]=t
			t=t+delta
		}

		
		
	}
}
